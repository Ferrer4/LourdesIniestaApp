package com.lourdesiniesta.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.lourdesiniesta.app.adapters.ProductoAdapter
import com.lourdesiniesta.app.databinding.FragmentCatalogoBinding
import com.lourdesiniesta.app.models.Producto

class CatalogoFragment : Fragment() {

    private var _binding: FragmentCatalogoBinding? = null
    private val binding get() = _binding!!

    private val adapter = ProductoAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvProductos.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProductos.adapter = adapter

        cargarProductos()
    }

    private fun cargarProductos() {
        binding.progressBar.visibility = View.VISIBLE

        FirebaseFirestore.getInstance()
            .collection("productos")
            .whereEqualTo("visible", true)
            .addSnapshotListener { snapshot, error ->
                if (!isAdded) return@addSnapshotListener
                binding.progressBar.visibility = View.GONE

                if (error != null) {
                    Toast.makeText(requireContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val lista = snapshot?.documents?.map { doc ->
                    Producto(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        descripcion = doc.getString("descripcion") ?: "",
                        precio = doc.getDouble("precio") ?: 0.0,
                        imagenURL = doc.getString("imagenURL") ?: "",
                        categoria = doc.getString("categoria") ?: "",
                        visible = doc.getBoolean("visible") ?: true
                    )
                } ?: emptyList()

                adapter.submitList(lista)
                binding.tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
