package com.example.bolsatrabajoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.R
import com.example.bolsatrabajoapp.entity.Oferta

class HistorialAdapter(
    private val listaOfertas: List<Oferta>,
    private val onItemClick: ((Oferta) -> Unit)? = null
) : RecyclerView.Adapter<HistorialAdapter.HistorialAdapterViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_oferta, parent, false)
        return HistorialAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialAdapterViewHolder, position: Int) {
        val oferta = listaOfertas[position]
        holder.tvEmpresa.text = oferta.empresa
        holder.tvTitulo.text  = oferta.titulo

        val sueldoTxt = when {
            oferta.salario_min > 0 && oferta.salario_max > 0 ->
                "S/. ${oferta.salario_min} - S/. ${oferta.salario_max}"
            oferta.salario_min > 0 -> "S/. ${oferta.salario_min}"
            oferta.salario_max > 0 -> "S/. ${oferta.salario_max}"
            else -> "A convenir"
        }
        holder.tvSueldo.text = sueldoTxt

        // runCatching { holder.iconEmpresa.setImageURI(Uri.parse(oferta.iconEmpresa)) }
        // else placeholder:
        holder.iconEmpresa.setImageResource(R.mipmap.ic_launcher_round)

        holder.itemView.setOnClickListener { onItemClick?.invoke(oferta) }
    }

    override fun getItemCount(): Int = listaOfertas.size

    inner class HistorialAdapterViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvEmpresa: TextView = itemView.findViewById(R.id.tvEmpresa)
        val tvTitulo : TextView = itemView.findViewById(R.id.tvTitulo)
        val tvSueldo : TextView = itemView.findViewById(R.id.tvSueldo)
        val iconEmpresa : ImageView = itemView.findViewById(R.id.iconEmpresa)
    }
}
