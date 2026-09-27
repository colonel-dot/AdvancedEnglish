package com.example.advancedenglish.ui

import android.graphics.Typeface
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.advancedenglish.R

class DifficulityAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<DifficulityAdapter.VH>() {

    private val items = ArrayList<Int>()
    var selected = 0

        set(value) {
            if (value == field) return
            val old = field
            field = value
            if (old in items.indices) notifyItemChanged(old)
            if (value in items.indices) notifyItemChanged(value)
        }

    fun submit(list: List<Int>, selectedValue: Int? = null) {
        items.clear()
        items.addAll(list)
        selected = selectedValue?.let { v -> items.indexOf(v).takeIf { it >= 0 } } ?: 0
        notifyDataSetChanged()
    }

    inner class VH(val tv: TextView) : RecyclerView.ViewHolder(tv) {
        init {
            tv.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                selected = pos
                onClick(items[pos])
            }
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): VH {
        val tv = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_difficulity, parent, false) as TextView
        return VH(tv)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val v = items[position]
        val selected = position == selected
        holder.tv.text = v.toString()
        holder.tv.setTextColor(
            ContextCompat.getColor(
                holder.tv.context,
                if (selected) R.color.brand_blue else R.color.text_sub
            )
        )
        holder.tv.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
    }
}
