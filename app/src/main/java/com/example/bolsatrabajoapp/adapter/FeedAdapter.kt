package com.example.bolsatrabajoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.R
import com.example.bolsatrabajoapp.data.FeedItem
import com.google.android.material.button.MaterialButton
import java.util.concurrent.TimeUnit

class FeedAdapter(
    private val items: List<FeedItem>,
    private val onVerOferta: (FeedItem) -> Unit,
    private val onVerUsuario: (FeedItem) -> Unit
) : RecyclerView.Adapter<FeedAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvLinea: TextView = v.findViewById(R.id.tvLinea)
        val tvFecha: TextView = v.findViewById(R.id.tvFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_event, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val it = items[pos]
        h.tvLinea.text = "${it.postulanteNombre} postuló a ${it.tituloOferta}"
        h.tvFecha.text = humanize(it.fechaUnix)
    }

    override fun getItemCount() = items.size

    private fun humanize(epochSec: Long): String {
        val diffMs = System.currentTimeMillis() - epochSec * 1000
        val min = TimeUnit.MILLISECONDS.toMinutes(diffMs)
        val hrs = TimeUnit.MILLISECONDS.toHours(diffMs)
        val days = TimeUnit.MILLISECONDS.toDays(diffMs)
        return when {
            min < 1 -> "justo ahora"
            min < 60 -> "hace ${min} min"
            hrs < 24 -> "hace ${hrs} h"
            else -> "hace ${days} d"
        }
    }
}
