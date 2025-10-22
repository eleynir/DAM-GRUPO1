package com.example.bolsatrabajoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.R
import com.example.bolsatrabajoapp.entity.Categoria

class CategoriaAdapter(
    private val categoriasList: MutableList<Categoria>
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    // Interfaz para manejar eventos fuera del adapter
    interface OnItemActionListener {
        fun onEditClick(categoria: Categoria)
        fun onDeleteClick(categoria: Categoria)
    }

    private var actionListener: OnItemActionListener? = null

    fun setOnItemActionListener(listener: OnItemActionListener) {
        actionListener = listener
    }

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreCategoria)
        val btnEditar: TextView = itemView.findViewById(R.id.btnEditarCategoria)
        val btnEliminar: TextView = itemView.findViewById(R.id.btnEliminarCategoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categoriasList[position]
        holder.tvNombre.text = categoria.descripcion

        holder.btnEditar.setOnClickListener {
            actionListener?.onEditClick(categoria)
        }

        holder.btnEliminar.setOnClickListener {
            actionListener?.onDeleteClick(categoria)
        }
    }

    override fun getItemCount(): Int = categoriasList.size

    fun updateData(newCategorias: List<Categoria>) {
        categoriasList.clear()
        categoriasList.addAll(newCategorias)
        notifyDataSetChanged()
    }
}
