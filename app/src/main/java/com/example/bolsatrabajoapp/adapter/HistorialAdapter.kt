package com.example.bolsatrabajoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.entity.Oferta
import com.example.bolsatrabajoapp.R

class HistorialAdapter (val listaOfertas : List<Oferta>) : RecyclerView.Adapter<HistorialAdapter.HistorialAdapterViewHolder>()  {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistorialAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_oferta, parent, false)
        return HistorialAdapterViewHolder (view)
    }

    override fun onBindViewHolder(
        holder: HistorialAdapterViewHolder,
        position: Int
    ) {
        val oferta : Oferta = listaOfertas[position]
        val empresa = oferta.empresa
        val titulo = oferta.titulo
        val sueldo = oferta.salario_min
        val iconEmpresa = oferta.icon_emp
        holder.tvEmpresa.text = empresa
        holder.tvTitulo.text = titulo
//        holder.tvSueldo.text = "S/.$sueldo"
        // holder.iconEmpresa.text = iconEmpresa
    }

    override fun getItemCount(): Int {
        return listaOfertas.size
    }

    inner class HistorialAdapterViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvEmpresa: TextView = itemView.findViewById(R.id.tvEmpresa)
        val tvTitulo : TextView = itemView.findViewById(R.id.tvTitulo)
//        val tvSueldo : TextView = itemView.findViewById(R.id.tvSueldo)
//        val iconEmpresa : ImageView = itemView.findViewById(R.id.iconEmpresa)

    }
}