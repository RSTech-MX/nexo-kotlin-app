package com.rstech.inventory.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.rstech.inventory.domain.model.Purchase

data class PurchaseDto(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("fecha de compra") @set:PropertyName("fecha de compra") var fechaCompra: String = "",
    @get:PropertyName("descripcion") @set:PropertyName("descripcion") var descripcion: String = "",
    @get:PropertyName("rfc provedor") @set:PropertyName("rfc provedor") var rfcProvedor: String = "",
    @get:PropertyName("provedor") @set:PropertyName("provedor") var provedor: String = "",
    @get:PropertyName("costo") @set:PropertyName("costo") var costo: Double = 0.0,
    @get:PropertyName("tipo de pago") @set:PropertyName("tipo de pago") var tipoPago: String = "",
    @get:PropertyName("tipo de tarjeta") @set:PropertyName("tipo de tarjeta") var tipoTarjeta: String = "",
    @get:PropertyName("ticket") @set:PropertyName("ticket") var ticket: String = "",
    @get:PropertyName("fecha de factura") @set:PropertyName("fecha de factura") var fechaFactura: String = "",
    @get:PropertyName("folio fiscal") @set:PropertyName("folio fiscal") var folioFiscal: String = "",
    @get:PropertyName("set") @set:PropertyName("set") var set: String = "",
    @get:PropertyName("imagen") @set:PropertyName("imagen") var imagen: String? = null,
    @get:PropertyName("fecha") @set:PropertyName("fecha") var fecha: Timestamp? = null
)

fun PurchaseDto.toDomain(): Purchase {
    return Purchase(
        id = id,
        fechaCompra = fechaCompra,
        descripcion = descripcion,
        rfcProveedor = rfcProvedor,   // Mapea al campo en español de tu modelo
        proveedor = provedor,         // Mapea al campo en español de tu modelo
        costo = costo,
        tipoPago = tipoPago,
        tipoTarjeta = tipoTarjeta,
        ticket = ticket,
        fechaFactura = fechaFactura,
        folioFiscal = folioFiscal,
        set = set,
        imagen = imagen,
        fecha = fecha?.toDate()
    )
}