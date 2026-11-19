package com.rstech.inventory.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rstech.inventory.domain.model.Product
import com.rstech.inventory.domain.usecase.DeleteProductUseCase
import com.rstech.inventory.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProductInventoryUiEvent {
    data class ShowMessage(val message: String) : ProductInventoryUiEvent
}

@HiltViewModel
class ProductInventoryViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<ProductInventoryUiEvent>()
    val uiEvent: SharedFlow<ProductInventoryUiEvent> = _uiEvent.asSharedFlow()

    val products: StateFlow<List<Product>> = getProductsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            deleteProductUseCase(productId)
                .onSuccess {
                    _uiEvent.emit(ProductInventoryUiEvent.ShowMessage("Producto eliminado"))
                }
                .onFailure { exception ->
                    _uiEvent.emit(
                        ProductInventoryUiEvent.ShowMessage(
                            "Error al eliminar: ${exception.localizedMessage}"
                        )
                    )
                }
        }
    }
}