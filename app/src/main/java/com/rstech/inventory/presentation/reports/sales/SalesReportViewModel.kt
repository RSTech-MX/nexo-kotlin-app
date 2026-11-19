package com.rstech.inventory.presentation.reports.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rstech.inventory.domain.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SalesReportViewModel @Inject constructor(
    private val saleRepository: SaleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesReportUiState())
    val uiState: StateFlow<SalesReportUiState> = _uiState.asStateFlow()

    init {
        loadSales()
    }

    fun setFilterType(filter: SalesFilterType) {
        _uiState.value = _uiState.value.copy(filterType = filter)
        loadSales()
    }

    fun setSelectedDate(date: Date) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadSales()
    }

    fun setPaymentMethod(method: String) {
        _uiState.value = _uiState.value.copy(paymentMethod = method)
        loadSales()
    }

    private fun loadSales() {
        val currentState = _uiState.value
        val (start, end) = calculateDateRanges(currentState.filterType, currentState.selectedDate)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            saleRepository.getSalesStream(
                startDate = start,
                endDate = end,
                paymentMethod = currentState.paymentMethod
            )
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar las ventas"
                    )
                }
                .collect { salesList ->
                    // Mapeamos los modelos de dominio asegurando la estructura correcta
                    val reportItems = salesList.map { sale ->
                        SaleReportItem(
                            id = sale.id,
                            date = sale.date ?: Date(),
                            paymentMethod = sale.paymentMethod,
                            total = sale.total,
                            products = sale.items.map { item ->
                                SaleDetailItem(
                                    name = item.name, // 🟢 Cambiado a 'name' (o 'nombre' según tu SaleItem)
                                    quantity = item.quantity,
                                    price = item.price,
                                    subtotal = item.subtotal
                                )
                            }
                        )
                    }

                    val total = reportItems.sumOf { it.total }
                    _uiState.value = _uiState.value.copy(
                        sales = reportItems,
                        grandTotal = total,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }

    private fun calculateDateRanges(filter: SalesFilterType, date: Date): Pair<Date, Date> {
        val cal = Calendar.getInstance().apply { time = date }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return when (filter) {
            SalesFilterType.DAY -> {
                val start = cal.time
                cal.add(Calendar.DAY_OF_MONTH, 1)
                val end = cal.time
                Pair(start, end)
            }

            SalesFilterType.MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = cal.time
                cal.add(Calendar.MONTH, 1)
                val end = cal.time
                Pair(start, end)
            }

            SalesFilterType.YEAR -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.MONTH, 0)
                val start = cal.time
                cal.add(Calendar.YEAR, 1)
                val end = cal.time
                Pair(start, end)
            }
        }
    }
}