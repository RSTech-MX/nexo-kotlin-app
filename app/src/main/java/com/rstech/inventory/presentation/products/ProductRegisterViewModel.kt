package com.rstech.inventory.presentation.products
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rstech.inventory.domain.model.Product
import com.rstech.inventory.domain.usecase.AddProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductRegisterViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductRegisterUiState())
    val uiState: StateFlow<ProductRegisterUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProductRegisterUiEvent>()
    val uiEvent: SharedFlow<ProductRegisterUiEvent> = _uiEvent.asSharedFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onSkuChange(value: String) {
        _uiState.update { it.copy(sku = value) }
    }

    fun onCategoryChange(value: String) {
        _uiState.update { it.copy(category = value) }
    }

    fun onCostChange(value: String) {
        _uiState.update { it.copy(cost = value) }
    }

    fun onPriceChange(value: String) {
        _uiState.update { it.copy(price = value) }
    }

    fun onQuantityChange(value: String) {
        _uiState.update { it.copy(quantity = value) }
    }

    fun onMinStockChange(value: String) {
        _uiState.update { it.copy(minStock = value) }
    }

    fun onUnitChange(value: String) {
        _uiState.update { it.copy(unit = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun saveProduct() {
        viewModelScope.launch {
            val current = _uiState.value

            if (
                current.name.isBlank() ||
                current.cost.isBlank() ||
                current.price.isBlank() ||
                current.quantity.isBlank()
            ) {
                _uiEvent.emit(
                    ProductRegisterUiEvent.ShowMessage("Por favor completa los campos obligatorios")
                )
                return@launch
            }

            val costDouble = current.cost.trim().toDoubleOrNull()
            val priceDouble = current.price.trim().toDoubleOrNull()
            val quantityInt = current.quantity.trim().toIntOrNull()
            val minStockInt = if (current.minStock.isBlank()) {
                0
            } else {
                current.minStock.trim().toIntOrNull()
            }

            if (
                costDouble == null ||
                priceDouble == null ||
                quantityInt == null ||
                minStockInt == null
            ) {
                _uiEvent.emit(
                    ProductRegisterUiEvent.ShowMessage("Verifica que los valores numéricos sean válidos")
                )
                return@launch
            }

            if (costDouble < 0.0 || priceDouble < 0.0 || quantityInt < 0 || minStockInt < 0) {
                _uiEvent.emit(
                    ProductRegisterUiEvent.ShowMessage("Los valores numéricos no pueden ser negativos")
                )
                return@launch
            }

            try {
                _uiState.update { it.copy(isSaving = true) }

                val product = Product(
                    name = current.name.trim(),
                    sku = current.sku.trim(),
                    category = current.category.trim(),
                    cost = costDouble,
                    price = priceDouble,
                    stock = quantityInt,
                    minStock = minStockInt,
                    unit = current.unit.trim().ifBlank { "unidad" },
                    description = current.description.trim()
                )

                val result = addProductUseCase(product)

                result.onSuccess {
                    _uiEvent.emit(ProductRegisterUiEvent.ShowMessage("Producto registrado con éxito"))
                    _uiState.value = ProductRegisterUiState()
                    _uiEvent.emit(ProductRegisterUiEvent.ProductRegistered)
                }.onFailure { exception ->
                    _uiEvent.emit(
                        ProductRegisterUiEvent.ShowMessage("Error: ${exception.localizedMessage}")
                    )
                }
            } catch (e: Exception) {
                _uiEvent.emit(
                    ProductRegisterUiEvent.ShowMessage("Error inesperado: ${e.localizedMessage}")
                )
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}