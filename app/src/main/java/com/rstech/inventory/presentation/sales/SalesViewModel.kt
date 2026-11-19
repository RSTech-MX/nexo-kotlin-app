package com.rstech.inventory.presentation.sales

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rstech.inventory.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ProductItem(
    val id: String,
    val nombre: String,
    val precio: Double,
    val stock: Int
)

data class CartItem(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
) {
    val subtotal: Double get() = precio * cantidad
}

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _carrito = mutableStateMapOf<String, CartItem>()
    val carrito: Map<String, CartItem> get() = _carrito

    var total by mutableStateOf(0.0)
        private set

    var metodoPago by mutableStateOf("")
        private set

    private val _productos = MutableStateFlow<List<ProductItem>>(emptyList())
    val productos: StateFlow<List<ProductItem>> = _productos.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Usamos getProductsStream() tal como está definido en tu ProductRepositoryImpl
                productRepository.getProductsStream()
                    .catch { e ->
                        e.printStackTrace()
                        _isLoading.value = false
                    }
                    .collect { list ->
                        _productos.value = list.map { prod ->
                            ProductItem(
                                id = prod.id,
                                nombre = prod.name,    // Mapeado con las propiedades de tu modelo Domain
                                precio = prod.price,
                                stock = prod.stock
                            )
                        }
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoading.value = false
            }
        }
    }

    fun agregarAlCarrito(id: String, nombre: String, precio: Double) {
        if (_carrito.containsKey(id)) {
            val current = _carrito[id]!!
            _carrito[id] = current.copy(cantidad = current.cantidad + 1)
        } else {
            _carrito[id] = CartItem(nombre = nombre, precio = precio, cantidad = 1)
        }
        recalcularTotal()
    }

    fun eliminarDelCarrito(id: String) {
        if (!_carrito.containsKey(id)) return
        val current = _carrito[id]!!
        if (current.cantidad > 1) {
            _carrito[id] = current.copy(cantidad = current.cantidad - 1)
        } else {
            _carrito.remove(id)
        }
        recalcularTotal()
    }

    private fun recalcularTotal() {
        total = _carrito.values.sumOf { it.subtotal }
    }

    fun actualizarMetodoPago(metodo: String) {
        metodoPago = metodo
    }

    suspend fun cobrar(): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            // Estructura para guardar en Firestore
            val productosMap = _carrito.mapKeys { it.key }.mapValues { entry ->
                mapOf(
                    "nombre" to entry.value.nombre,
                    "precio" to entry.value.precio,
                    "cantidad" to entry.value.cantidad
                )
            }

            val ventaData = mapOf(
                "productos" to productosMap,
                "total" to total,
                "metodoPago" to metodoPago,
                "fecha" to Timestamp.now()
            )

            // 1. Guardar la venta en la subcolección del usuario
            firestore.collection("usuarios").document(uid)
                .collection("ventas").add(ventaData).await()

            // 2. Actualizar (restar) el stock de cada producto comprado en Firestore
            val batch = firestore.batch()
            for ((productId, cartItem) in _carrito) {
                val productRef = firestore.collection("usuarios").document(uid)
                    .collection("productos").document(productId)

                // Resta la cantidad vendida al stock actual de forma segura y atómica
                batch.update(productRef, "cantidad", com.google.firebase.firestore.FieldValue.increment(-cartItem.cantidad.toLong()))
            }
            batch.commit().await()

            // 3. Limpiar carrito y restablecer estados
            _carrito.clear()
            total = 0.0
            metodoPago = ""

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}