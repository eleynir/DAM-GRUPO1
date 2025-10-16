package com.example.bolsatrabajoapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.databinding.ItemOfertaBinding
import com.example.bolsatrabajoapp.entity.Oferta
import java.text.NumberFormat
import java.util.*

class OfertasAdapter(
    private val ofertas: List<Oferta>,
    private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<OfertasAdapter.OfertaViewHolder>() {

    // Define la interfaz para manejar el clic
    interface OnItemClickListener {
        fun onItemClick(oferta: Oferta)
    }

    // Formateador de moneda (ajustar Locale si es necesario)
    private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "PE")).apply {
        maximumFractionDigits = 0
    }

    inner class OfertaViewHolder(private val binding: ItemOfertaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(oferta: Oferta) {
            binding.apply {
                // Título y Empresa
                tvTitulo.text = oferta.titulo
                tvEmpresa.text = oferta.empresa

                // Ubicación y Modalidad
                tvUbicacion.text = oferta.ubicacion
                tvModalidad.text = oferta.modalidad

                // Salario
                val salarioMinFormatted = currencyFormatter.format(oferta.salario_min)
                val salarioMaxFormatted = currencyFormatter.format(oferta.salario_max)
                tvSalario.text = "$salarioMinFormatted - $salarioMaxFormatted"

                // Icono (Puedes usar Glide o Picasso para cargar el iconoEmpresa real)
                // Por ahora, solo se usa el drawable por defecto del XML si la imagen es remota.

                // Manejo del click en el item
                itemView.setOnClickListener {
                    clickListener.onItemClick(oferta)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertaViewHolder {
        val binding = ItemOfertaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OfertaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OfertaViewHolder, position: Int) {
        holder.bind(ofertas[position])
    }

    override fun getItemCount(): Int = ofertas.size
}