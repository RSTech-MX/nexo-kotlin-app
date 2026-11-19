package com.rstech.inventory.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rstech.inventory.data.remote.dto.PurchaseDto
import com.rstech.inventory.data.remote.dto.toDomain
import com.rstech.inventory.domain.model.Purchase
import com.rstech.inventory.domain.repository.PurchaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PurchaseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : PurchaseRepository {

    private val currentUid: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    private val purchasesRef
        get() = firestore.collection("usuarios").document(currentUid).collection("compras")

    override suspend fun addPurchase(purchase: Purchase): Result<Unit> = runCatching {
        val purchaseData = hashMapOf<String, Any?>(
            "fecha de compra" to purchase.fechaCompra,
            "descripcion" to purchase.descripcion,
            "rfc provedor" to purchase.rfcProveedor,
            "provedor" to purchase.proveedor,
            "costo" to purchase.costo,
            "tipo de pago" to purchase.tipoPago,
            "tipo de tarjeta" to purchase.tipoTarjeta,
            "ticket" to purchase.ticket,
            "fecha de factura" to purchase.fechaFactura,
            "folio fiscal" to purchase.folioFiscal,
            "set" to purchase.set,
            "imagen" to purchase.imagen,
            "fecha" to Timestamp.now()
        )
        purchasesRef.add(purchaseData).await()
    }

    override fun getPurchasesStream(): Flow<List<Purchase>> = callbackFlow {
        val listener = purchasesRef
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val purchases = snapshot?.documents?.mapNotNull { doc ->
                    val dto = doc.toObject(PurchaseDto::class.java)
                    dto?.id = doc.id
                    dto?.toDomain()
                } ?: emptyList()

                trySend(purchases)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getAllPurchases(): Result<List<Purchase>> = runCatching {
        val snapshot = purchasesRef
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .await()

        snapshot.documents.mapNotNull { doc ->
            val dto = doc.toObject(PurchaseDto::class.java)
            dto?.id = doc.id
            dto?.toDomain()
        }
    }
}