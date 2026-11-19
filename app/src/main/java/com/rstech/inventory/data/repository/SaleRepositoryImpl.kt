package com.rstech.inventory.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rstech.inventory.data.remote.dto.SaleDto
import com.rstech.inventory.data.remote.dto.SaleItemDto
import com.rstech.inventory.data.remote.dto.toDomain
import com.rstech.inventory.domain.model.Sale
import com.rstech.inventory.domain.model.SaleItem
import com.rstech.inventory.domain.repository.SaleRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class SaleRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : SaleRepository {

    private val currentUid: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    private val salesRef
        get() = firestore.collection("usuarios").document(currentUid).collection("ventas")

    private val productsRef
        get() = firestore.collection("usuarios").document(currentUid).collection("productos")

    override suspend fun registerSale(
        items: List<SaleItem>,
        total: Double,
        paymentMethod: String
    ): Result<Unit> = runCatching {
        val newSaleRef = salesRef.document()

        val saleData = hashMapOf(
            "total" to total,
            "metodo_pago" to paymentMethod,
            "fecha" to Timestamp.now(),
            "cantidad_productos" to items.size
        )

        newSaleRef.set(saleData).await()

        for (item in items) {
            val productDocRef = productsRef.document(item.productId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(productDocRef)

                if (!snapshot.exists()) {
                    throw IllegalStateException("El producto '${item.name}' ya no existe")
                }

                val currentStock = snapshot.getLong("cantidad")?.toInt() ?: 0

                if (currentStock < item.quantity) {
                    throw IllegalStateException("Stock insuficiente para '${item.name}'. Disponible: $currentStock")
                }

                transaction.update(productDocRef, "cantidad", currentStock - item.quantity)

                val detailRef = newSaleRef.collection("detalle").document()
                val detailData = hashMapOf(
                    "producto_id" to item.productId,
                    "nombre" to item.name,
                    "precio" to item.price,
                    "cantidad" to item.quantity,
                    "subtotal" to (item.price * item.quantity)
                )
                transaction.set(detailRef, detailData)
            }.await()
        }
    }

    override fun getSalesStream(
        startDate: Date,
        endDate: Date,
        paymentMethod: String
    ): Flow<List<Sale>> = callbackFlow {
        var query: Query = salesRef
            .whereGreaterThanOrEqualTo("fecha", Timestamp(startDate))
            .whereLessThan("fecha", Timestamp(endDate))

        if (paymentMethod != "Todos") {
            query = query.whereEqualTo("metodo_pago", paymentMethod)
        }

        val listener = query.orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val salesList = snapshot?.documents?.mapNotNull { doc ->
                    val dto = doc.toObject(SaleDto::class.java)
                    dto?.id = doc.id
                    dto?.toDomain()
                } ?: emptyList()

                trySend(salesList)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getSalesWithDetails(
        startDate: Date,
        endDate: Date,
        paymentMethod: String
    ): Result<List<Sale>> = runCatching {
        var query: Query = salesRef
            .whereGreaterThanOrEqualTo("fecha", Timestamp(startDate))
            .whereLessThan("fecha", Timestamp(endDate))

        if (paymentMethod != "Todos") {
            query = query.whereEqualTo("metodo_pago", paymentMethod)
        }

        val salesSnap = query.orderBy("fecha", Query.Direction.DESCENDING).get().await()

        val salesList = mutableListOf<Sale>()

        for (doc in salesSnap.documents) {
            val saleDto = doc.toObject(SaleDto::class.java) ?: continue
            saleDto.id = doc.id

            val detailSnap = doc.reference.collection("detalle").get().await()
            val items = detailSnap.documents.mapNotNull { detailDoc ->
                detailDoc.toObject(SaleItemDto::class.java)?.toDomain()
            }

            salesList.add(saleDto.toDomain(items))
        }

        salesList
    }
}