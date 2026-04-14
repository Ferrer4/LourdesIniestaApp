package com.lourdesiniesta.app.models

import com.google.firebase.Timestamp

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val imagenURL: String = "",
    val categoria: String = "",
    val visible: Boolean = true
)

data class Servicio(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val duracionMinutos: Int = 0,
    val precio: Double = 0.0,
    val imagenURL: String = ""
)

data class Videoclase(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val miniaturaURL: String = "",
    val videoURL: String = "",
    val fechaPublicacion: Timestamp? = null
)

data class Cita(
    val id: String = "",
    val clienteID: String = "",
    val servicioID: String = "",
    val servicioNombre: String = "",
    val fecha: String = "",
    val hora: String = "",
    val estado: String = "pendiente"
)

data class Compra(
    val id: String = "",
    val clienteID: String = "",
    val tipo: String = "",
    val itemID: String = "",
    val itemNombre: String = "",
    val precio: Double = 0.0,
    val fecha: Timestamp? = null,
    val estado: String = "pendiente"
)

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val rol: String = "cliente"
)
