package com.lourdesiniesta.app.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lourdesiniesta.app.R
import com.lourdesiniesta.app.databinding.FragmentCitasBinding
import com.lourdesiniesta.app.models.Servicio
import java.util.Calendar

class CitasFragment : Fragment() {

    private var _binding: FragmentCitasBinding? = null
    private val binding get() = _binding!!

    private val servicios = mutableListOf<Servicio>()
    private var fechaSeleccionada = ""
    private var horaSeleccionada = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargarServicios()

        binding.btnFecha.setOnClickListener { mostrarDatePicker() }
        binding.btnHora.setOnClickListener { mostrarTimePicker() }
        binding.btnReservar.setOnClickListener { reservarCita() }
    }

    private fun cargarServicios() {
        FirebaseFirestore.getInstance().collection("servicios").get()
            .addOnSuccessListener { snapshot ->
                if (!isAdded) return@addOnSuccessListener
                servicios.clear()
                snapshot.documents.forEach { doc ->
                    servicios.add(
                        Servicio(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            descripcion = doc.getString("descripcion") ?: "",
                            duracionMinutos = (doc.getLong("duracionMinutos") ?: 0L).toInt(),
                            precio = doc.getDouble("precio") ?: 0.0
                        )
                    )
                }
                val nombres = servicios.map { "${it.nombre} – ${String.format("%.2f", it.precio)} €" }
                val spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    nombres
                )
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerServicios.adapter = spinnerAdapter
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "No se pudieron cargar los servicios", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDatePicker() {
        val cal = Calendar.getInstance()
        // No permitir fechas pasadas
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                fechaSeleccionada = "%04d-%02d-%02d".format(year, month + 1, day)
                binding.btnFecha.text = fechaSeleccionada
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).also { dialog ->
            dialog.datePicker.minDate = cal.timeInMillis
        }.show()
    }

    private fun mostrarTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                horaSeleccionada = "%02d:%02d".format(hour, minute)
                binding.btnHora.text = horaSeleccionada
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun reservarCita() {
        val idx = binding.spinnerServicios.selectedItemPosition
        if (servicios.isEmpty() || idx < 0) {
            Toast.makeText(requireContext(), "Selecciona un servicio", Toast.LENGTH_SHORT).show()
            return
        }
        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show()
            return
        }
        if (horaSeleccionada.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona una hora", Toast.LENGTH_SHORT).show()
            return
        }

        val servicio = servicios[idx]
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val cita = hashMapOf(
            "clienteID" to uid,
            "servicioID" to servicio.id,
            "servicioNombre" to servicio.nombre,
            "fecha" to fechaSeleccionada,
            "hora" to horaSeleccionada,
            "estado" to "pendiente"
        )

        binding.btnReservar.isEnabled = false
        FirebaseFirestore.getInstance().collection("citas").add(cita)
            .addOnSuccessListener {
                if (!isAdded) return@addOnSuccessListener
                binding.btnReservar.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    "¡Cita reservada! Lourdes te confirmará pronto.",
                    Toast.LENGTH_LONG
                ).show()
                // Reset
                fechaSeleccionada = ""
                horaSeleccionada = ""
                binding.btnFecha.text = getString(R.string.seleccionar_fecha)
                binding.btnHora.text = getString(R.string.seleccionar_hora)
            }
            .addOnFailureListener { e ->
                if (!isAdded) return@addOnFailureListener
                binding.btnReservar.isEnabled = true
                Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
