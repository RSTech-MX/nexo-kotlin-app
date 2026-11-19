package com.rstech.inventory.presentation.purchase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rstech.inventory.domain.model.Purchase // Asegúrate de que apunte a tu modelo de dominio Purchase
import com.rstech.inventory.domain.usecase.AddPurchaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed class PurchaseRegisterUiEvent {
    data class ShowMessage(val message: String) : PurchaseRegisterUiEvent()
    object PurchaseRegistered : PurchaseRegisterUiEvent()
}

@HiltViewModel
class PurchaseRegisterViewModel @Inject constructor(
    private val addPurchaseUseCase: AddPurchaseUseCase
) : ViewModel() {

    // =============================
    // Controladores de estado para los campos de texto
    // =============================
    var fechaCompra by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var rfcProveedor by mutableStateOf("")
    var proveedor by mutableStateOf("")
    var costo by mutableStateOf("")
    var tipoPago by mutableStateOf("")
    var tipoTarjeta by mutableStateOf("")
    var ticket by mutableStateOf("")
    var fechaFactura by mutableStateOf("")
    var folioFiscal by mutableStateOf("")
    var set by mutableStateOf("")

    var guardando by mutableStateOf(false)
        private set

    private val _uiEvent = MutableSharedFlow<PurchaseRegisterUiEvent>()
    val uiEvent: SharedFlow<PurchaseRegisterUiEvent> = _uiEvent.asSharedFlow()

    // =============================
    // Método para guardar la compra (conectado al UseCase)
    // =============================
    fun registrarCompra() {
        viewModelScope.launch {
            // Validaciones básicas previas
            if (descripcion.isBlank() || costo.isBlank() || proveedor.isBlank()) {
                _uiEvent.emit(PurchaseRegisterUiEvent.ShowMessage("Por favor completa los campos obligatorios"))
                return@launch
            }

            val costoDouble = costo.toDoubleOrNull()
            if (costoDouble == null) {
                _uiEvent.emit(PurchaseRegisterUiEvent.ShowMessage("El costo debe ser un valor numérico válido"))
                return@launch
            }

            try {
                guardando = true

                // Creamos el objeto Purchase usando los nombres exactos en español de tu modelo
                val purchase = Purchase(
                    fechaCompra = fechaCompra,
                    descripcion = descripcion,
                    rfcProveedor = rfcProveedor,
                    proveedor = proveedor,
                    costo = costoDouble,
                    tipoPago = tipoPago,
                    tipoTarjeta = tipoTarjeta,
                    ticket = ticket,
                    fechaFactura = fechaFactura,
                    folioFiscal = folioFiscal,
                    set = set,
                    imagen = null, // O tu variable de imagen si ya la implementaste
                    fecha = Date()
                )

                // Invocamos tu AddPurchaseUseCase
                val result = addPurchaseUseCase(purchase)

                result.onSuccess {
                    _uiEvent.emit(PurchaseRegisterUiEvent.ShowMessage("Producto registrado con éxito"))
                    limpiarFormulario()
                    _uiEvent.emit(PurchaseRegisterUiEvent.PurchaseRegistered)
                }.onFailure { exception ->
                    _uiEvent.emit(PurchaseRegisterUiEvent.ShowMessage("Error: ${exception.localizedMessage}"))
                }

            } catch (e: Exception) {
                _uiEvent.emit(PurchaseRegisterUiEvent.ShowMessage("Error inesperado: ${e.localizedMessage}"))
            } finally {
                guardando = false
            }
        }
    }

    // =============================
    // Limpieza de campos (Equivalente al clear de Flutter)
    // =============================
    private fun limpiarFormulario() {
        fechaCompra = ""
        descripcion = ""
        rfcProveedor = ""
        proveedor = ""
        costo = ""
        tipoPago = ""
        tipoTarjeta = ""
        ticket = ""
        fechaFactura = ""
        folioFiscal = ""
        set = ""
    }
}