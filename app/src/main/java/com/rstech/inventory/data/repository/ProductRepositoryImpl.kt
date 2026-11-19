package com.rstech.inventory.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rstech.inventory.data.remote.dto.ProductDto
import com.rstech.inventory.data.remote.dto.toDomain
import com.rstech.inventory.domain.model.Product
import com.rstech.inventory.domain.repository.ProductRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ProductRepository {

    private val currentUid: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    private val productsRef
        get() = firestore.collection("usuarios").document(currentUid).collection("productos")

    override fun getProductsStream(): Flow<List<Product>> = callbackFlow {
        val listener = productsRef
            .orderBy("nombre", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val products = snapshot?.documents?.mapNotNull { doc ->
                    val dto = doc.toObject(ProductDto::class.java)
                    dto?.id = doc.id
                    dto?.toDomain()
                } ?: emptyList()

                trySend(products)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getProductsOnce(): Result<List<Product>> = runCatching {
        val snapshot = productsRef
            .orderBy("nombre", Query.Direction.ASCENDING)
            .get()
            .await()

        snapshot.documents.mapNotNull { doc ->
            val dto = doc.toObject(ProductDto::class.java)
            dto?.id = doc.id
            dto?.toDomain()
        }
    }

    override suspend fun addProduct(product: Product): Result<Unit> = runCatching {
        val now = Timestamp.now()
        val productData = hashMapOf<String, Any>(
            "nombre" to product.name,
            "sku" to product.sku,
            "categoria" to product.category,
            "costo" to product.cost,
            "precio" to product.price,
            "cantidad" to product.stock,
            "stock_minimo" to product.minStock,
            "unidad" to product.unit.ifBlank { "unidad" },
            "descripcion" to product.description,
            "activo" to product.isActive,
            "fecha_creacion" to now,
            "fecha_actualizacion" to now
        )
        productsRef.add(productData).await()
    }

    override suspend fun updateProduct(product: Product): Result<Unit> = runCatching {
        val updateData = hashMapOf<String, Any>(
            "nombre" to product.name,
            "sku" to product.sku,
            "categoria" to product.category,
            "costo" to product.cost,
            "precio" to product.price,
            "cantidad" to product.stock,
            "stock_minimo" to product.minStock,
            "unidad" to product.unit.ifBlank { "unidad" },
            "descripcion" to product.description,
            "activo" to product.isActive,
            "fecha_actualizacion" to Timestamp.now()
        )
        productsRef.document(product.id).update(updateData).await()
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> = runCatching {
        productsRef.document(productId).delete().await()
    }
}