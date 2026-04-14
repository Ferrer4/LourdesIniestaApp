package com.lourdesiniesta.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lourdesiniesta.app.adapters.VideoclaseAdapter
import com.lourdesiniesta.app.databinding.FragmentVideoclasesBinding
import com.lourdesiniesta.app.models.Videoclase

class VideoclasesFragment : Fragment() {

    private var _binding: FragmentVideoclasesBinding? = null
    private val binding get() = _binding!!

    private val idsCompradas = mutableSetOf<String>()
    private lateinit var adapter: VideoclaseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoclasesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VideoclaseAdapter(idsCompradas)
        binding.rvVideoclases.adapter = adapter

        cargarDatos()
    }

    private fun cargarDatos() {
        binding.progressBar.visibility = View.VISIBLE
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // 1. Cargar compras del usuario
        FirebaseFirestore.getInstance()
            .collection("compras")
            .whereEqualTo("clienteID", uid)
            .whereEqualTo("tipo", "videoclase")
            .whereEqualTo("estado", "pagado")
            .get()
            .addOnCompleteListener { task ->
                if (!isAdded) return@addOnCompleteListener
                idsCompradas.clear()
                if (task.isSuccessful) {
                    task.result?.documents?.forEach { doc ->
                        val itemID = doc.getString("itemID") ?: ""
                        if (itemID.isNotEmpty()) idsCompradas.add(itemID)
                    }
                }
                // 2. Cargar videoclases
                cargarVideoclases()
            }
    }

    private fun cargarVideoclases() {
        FirebaseFirestore.getInstance()
            .collection("videoclases")
            .orderBy("fechaPublicacion", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!isAdded) return@addOnSuccessListener
                binding.progressBar.visibility = View.GONE
                val lista = snapshot.documents.map { doc ->
                    Videoclase(
                        id = doc.id,
                        titulo = doc.getString("titulo") ?: "",
                        descripcion = doc.getString("descripcion") ?: "",
                        precio = doc.getDouble("precio") ?: 0.0,
                        miniaturaURL = doc.getString("miniaturaURL") ?: "",
                        videoURL = doc.getString("videoURL") ?: "",
                        fechaPublicacion = doc.getTimestamp("fechaPublicacion")
                    )
                }
                adapter.submitList(lista)
                binding.tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                if (!isAdded) return@addOnFailureListener
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error al cargar videoclases", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
