package com.rstech.inventory.domain.model

import java.util.Date

data class Purchase(
    val id: String = "",
    val fechaCompra: String,        // "fecha de compra"
    val descripcion: String,        // "descripcion"
    val rfcProveedor: String,       // "rfc provedor"
    val proveedor: String,          // "provedor"
    val costo: Double,              // "costo"
    val tipoPago: String,           // "tipo de pago"
    val tipoTarjeta: String,        // "tipo de tarjeta"
    val ticket: String,             // "ticket"
    val fechaFactura: String,       // "fecha de factura"
    val folioFiscal: String,        // "folio fiscal"
    val set: String,                // "set"
    val imagen: String? = null,     // "imagen"
    val fecha: Date? = null         // "fecha" de registro
)