package com.spicyfy.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.spicyfy.app.R

data class QuickAccessItem(
    val title: String,
    val coverUrl: String?,
    val onClick: () -> Unit
)

class QuickAccessAdapter(
    private val items: List<QuickAccessItem>
) : RecyclerView.Adapter<QuickAccessAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: ImageView = itemView.findViewById(R.id.quick_cover)
        val title: TextView = itemView.findViewById(R.id.quick_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quick_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.cover.load(item.coverUrl) {
            crossfade(true)
            placeholder(R.drawable.gradient_card_small)
            error(R.drawable.gradient_card_small)
        }
        holder.itemView.setOnClickListener { item.onClick() }
    }

    override fun getItemCount(): Int = items.size
}
