package com.example.advancedenglish.ui

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.advancedenglish.R
import com.example.advancedenglish.model.ArticleType

class CategoryAdapter(
    private val onClick: (position: Int, ArticleType) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.VH>() {

    private val items = ArrayList<ArticleType>()
    var selected = 0
        set(value) {
            if (value == field) return
            val old = field
            field = value
            if (old in items.indices) notifyItemChanged(old)
            if (value in items.indices) notifyItemChanged(value)
        }

    fun submit(list: List<ArticleType>, selectedPos: Int = 0) {
        items.clear()
        items.addAll(list)
        selected = selectedPos
        notifyDataSetChanged()
    }

    inner class VH(val tv: TextView) : RecyclerView.ViewHolder(tv) {
        init {
            tv.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                selected = pos
                onClick(pos, items[pos])
            }
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): VH {
        val tv = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false) as TextView
        tv.compoundDrawablePadding = 8
        return VH(tv)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val selected = position == selected
        holder.tv.text = item.name
        holder.tv.setTextColor(
            ContextCompat.getColor(
                holder.tv.context,
                if (selected) R.color.brand_blue else R.color.text_sub
            )
        )
        holder.tv.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
        if (selected) {
            holder.tv.setBackgroundResource(R.drawable.bg_category_selected)
        } else {
            holder.tv.setBackgroundColor(Color.TRANSPARENT)
            holder.tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
}
