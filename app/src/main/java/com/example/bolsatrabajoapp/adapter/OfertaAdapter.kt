package com.example.bolsatrabajoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.R
import com.example.bolsatrabajoapp.entity.Oferta

class OfertaAdapter(private val listaOfertas: MutableList<Oferta>) :
    RecyclerView.Adapter<OfertaAdapter.OfertaViewHolder>() {

    inner class OfertaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIconEmpresa: ImageView = itemView.findViewById(R.id.ivIconEmpresa)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvEmpresa: TextView = itemView.findViewById(R.id.tvEmpresa)
        val tvUbicacion: TextView = itemView.findViewById(R.id.tvUbicacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_oferta, parent, false)
        return OfertaViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfertaViewHolder, position: Int) {
        val oferta = listaOfertas[position]
        holder.tvTitulo.text = oferta.titulo
        holder.tvEmpresa.text = oferta.empresa
        holder.tvUbicacion.text = oferta.ubicacion

        // Si tienes un icono guardado, podrías mostrarlo aquí
        // (por ahora solo usamos un ícono fijo)
        holder.ivIconEmpresa.setImageResource(com.example.bolsatrabajoapp.R.drawable.ic_business)
    }

    override fun getItemCount(): Int = listaOfertas.size
}