package com.lourdesiniesta.app.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lourdesiniesta.app.R
import com.lourdesiniesta.app.databinding.ItemVideoclaseBinding
import com.lourdesiniesta.app.models.Videoclase

class VideoclaseAdapter(
    private val idsCompradas: Set<String>
) : ListAdapter<Videoclase, VideoclaseAdapter.ViewHolder>(DiffCallback()) {

    private val WHATSAPP = "34654659135"

    inner class ViewHolder(val binding: ItemVideoclaseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoclaseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val v = getItem(position)
        val comprada = idsCompradas.contains(v.id)

        with(holder.binding) {
            tvTitulo.text = v.titulo
            tvDescripcion.text = v.descripcion
            tvPrecio.text = String.format("%.2f €", v.precio)

            Glide.with(ivMiniatura.context)
                .load(v.miniaturaURL.ifEmpty { null })
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(ivMiniatura)

            if (comprada) {
                btnAccion.text = "▶  Ver videoclase"
                btnAccion.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        btnAccion.context.getColor(R.color.price_green)
                    )
                btnAccion.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(v.videoURL))
                    btnAccion.context.startActivity(intent)
                }
            } else {
                btnAccion.text = "Comprar por WhatsApp"
                btnAccion.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        btnAccion.context.getColor(R.color.colorAccent)
                    )
                btnAccion.setOnClickListener {
                    val msg = "Hola Lourdes, me interesa la videoclase \"${v.titulo}\" " +
                            "(${String.format("%.2f", v.precio)} €). ¿Cómo puedo adquirirla?"
                    val url = "https://wa.me/$WHATSAPP?text=${Uri.encode(msg)}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    btnAccion.context.startActivity(intent)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Videoclase>() {
        override fun areItemsTheSame(a: Videoclase, b: Videoclase) = a.id == b.id
        override fun areContentsTheSame(a: Videoclase, b: Videoclase) = a == b
    }
}
