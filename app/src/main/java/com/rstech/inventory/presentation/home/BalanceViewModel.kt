package com.rstech.inventory.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _ingresos = MutableStateFlow(0.0)
    val ingresos: StateFlow<Double> = _ingresos.asStateFlow()

    private val _gastos = MutableStateFlow(0.0)
    val gastos: StateFlow<Double> = _gastos.asStateFlow()

    private val _balanceTotal = MutableStateFlow(0.0)
    val balanceTotal: StateFlow<Double> = _balanceTotal.asStateFlow()

    private val _porcentajeCambio = MutableStateFlow(0.0)
    val porcentajeCambio: StateFlow<Double> = _porcentajeCambio.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _nombreMes = MutableStateFlow("")
    val nombreMes: StateFlow<String> = _nombreMes.asStateFlow()

    private val meses = listOf(
        "enero", "febrero", "marzo", "abril", "mayo", "junio",
        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
    )

    init {
        cargarBalance()
    }

    fun cargarBalance() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = auth.currentUser ?: return@launch
                val uid = user.uid

                val calendar = Calendar.getInstance()
                val currentMonth = calendar.get(Calendar.MONTH) // 0-11
                val currentYear = calendar.get(Calendar.YEAR)

                _nombreMes.value = meses[currentMonth]

                // Rango mes actual
                calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
                val inicioMesActual = calendar.time
                calendar.add(Calendar.MONTH, 1)
                val finMesActual = calendar.time

                // Rango mes anterior
                calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
                calendar.add(Calendar.MONTH, -1)
                val inicioMesAnterior = calendar.time
                calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
                val finMesAnterior = inicioMesActual

                // Consultas mes actual
                val ingAct = sumarColeccion(uid, "ventas", "total", inicioMesActual, finMesActual)
                val gasAct = sumarColeccion(uid, "compras", "costo", inicioMesActual, finMesActual)

                _ingresos.value = ingAct
                _gastos.value = gasAct
                val totalBalance = ingAct - gasAct
                _balanceTotal.value = totalBalance

                // Consultas mes anterior para variación
                val ingAnt = sumarColeccion(uid, "ventas", "total", inicioMesAnterior, finMesAnterior)
                val gasAnt = sumarColeccion(uid, "compras", "costo", inicioMesAnterior, finMesAnterior)
                val balanceAnterior = ingAnt - gasAnt

                if (balanceAnterior == 0.0) {
                    _porcentajeCambio.value = if (totalBalance > 0) 100.0 else 0.0
                } else {
                    _porcentajeCambio.value = ((totalBalance - balanceAnterior) / abs(balanceAnterior)) * 100
                }

            } catch (e: Exception) {
                // Manejo de errores
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun sumarColeccion(
        uid: String,
        coleccion: String,
        campoMonto: String,
        inicio: Date,
        fin: Date
    ): Double {
        return try {
            val query = firestore.collection("usuarios")
                .document(uid)
                .collection(coleccion)
                .whereGreaterThanOrEqualTo("fecha", inicio)
                .whereLessThan("fecha", fin)
                .get()
                .await()

            var total = 0.0
            for (doc in query.documents) {
                val monto = doc.getDouble(campoMonto) ?: doc.getLong(campoMonto)?.toDouble() ?: 0.0
                total += monto
            }
            total
        } catch (e: Exception) {
            0.0
        }
    }
}