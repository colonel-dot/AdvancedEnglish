package com.example.advancedenglish.ui

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.advancedenglish.R
import com.example.advancedenglish.model.Article
import kotlin.math.abs

class ArticleAdapter(
    private val onClick: (Article) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = ArrayList<Article>()

    var footerState = FOOTER_IDLE
        set(value) {
            val old = field
            field = value
            if (old == value) return
            if (itemCount > 0) notifyItemChanged(itemCount - 1)
        }

    companion object {
        private const val TYPE_ARTICLE = 0
        private const val TYPE_FOOTER = 1
        const val FOOTER_IDLE = 0
        const val FOOTER_LOADING = 1
        const val FOOTER_NO_MORE = 2
        private val TAG_COLORS = intArrayOf(
            R.color.tag_teal, R.color.tag_green, R.color.tag_orange,
            R.color.tag_pink, R.color.tag_purple, R.color.tag_blue, R.color.tag_gray
        )
    }

    fun reset(list: List<Article>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        if (position == items.size) TYPE_FOOTER else TYPE_ARTICLE

    override fun getItemCount(): Int = items.size + 1

    inner class ArticleVH(v: View) : RecyclerView.ViewHolder(v) {
        val ivCover: ImageView = v.findViewById(R.id.ivCover)
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvDifficulty: TextView = v.findViewById(R.id.tvDifficulty)
        val tvWords: TextView = v.findViewById(R.id.tvWords)
        val tvTag: TextView = v.findViewById(R.id.tvTag)
        val tvAge: TextView = v.findViewById(R.id.tvAge)

        init {
            v.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION || pos >= items.size) return@setOnClickListener
                onClick(items[pos])
            }
        }
    }

    inner class FootVH(v: View) : RecyclerView.ViewHolder(v) {
        val progress: ProgressBar = v.findViewById(R.id.progress)
        val tvFooter: TextView = v.findViewById(R.id.tvFooter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_ARTICLE) {
            ArticleVH(inflater.inflate(R.layout.item_article, parent, false))
        } else {
            FootVH(inflater.inflate(R.layout.item_article_footer, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FootVH) {
            when (footerState) {
                FOOTER_LOADING -> {
                    holder.progress.visibility = View.VISIBLE
                    holder.tvFooter.text = "加载中"
                }
                FOOTER_NO_MORE -> {
                    holder.progress.visibility = View.GONE
                    holder.tvFooter.text = "没有更多了"
                }
                else -> {
                    holder.progress.visibility = View.GONE
                    holder.tvFooter.text = ""
                }
            }
            return
        }
        holder as ArticleVH
        val a = items[position]
        holder.tvTitle.text = a.title
        holder.tvDifficulty.text = "难度:${a.difficulity}"
        holder.tvWords.text = "${a.wordCount}词"

        Glide.with(holder.itemView.context)
            .load(a.cover)
            .placeholder(R.color.divider)
            .centerCrop()
            .into(holder.ivCover)

        if (a.typeName.isBlank()) {
            holder.tvTag.visibility = View.GONE
        } else {
            holder.tvTag.visibility = View.VISIBLE
            holder.tvTag.text = a.typeName
            val colorRes = TAG_COLORS[abs(a.typeName.hashCode()) % TAG_COLORS.size]
            val d = GradientDrawable()
            d.setColor(ContextCompat.getColor(holder.itemView.context, colorRes))
            d.cornerRadius = 10f * holder.itemView.resources.displayMetrics.density
            holder.tvTag.background = d
        }

        if (a.age.isNullOrBlank()) {
            holder.tvAge.visibility = View.GONE
        } else {
            holder.tvAge.visibility = View.VISIBLE
            holder.tvAge.text = a.age
        }
    }
}
