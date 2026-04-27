package com.moneyassist.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moneyassist.app.R

data class Article(
    val id: Int,
    val title: String,
    val category: String,
    val readMinutes: Int,
    val excerpt: String,
    var saved: Boolean = false
)

class ArticleAdapter(
    private val onSave: (Article) -> Unit,
    private val onRead: (Article) -> Unit
) : ListAdapter<Article, ArticleAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(a: Article, b: Article) = a.id == b.id
            override fun areContentsTheSame(a: Article, b: Article) = a == b
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvArticleTitle)
        val tvCategory: TextView = view.findViewById(R.id.tvArticleCategory)
        val tvTime: TextView = view.findViewById(R.id.tvArticleTime)
        val tvExcerpt: TextView = view.findViewById(R.id.tvArticleExcerpt)
        val tvRead: TextView = view.findViewById(R.id.tvArticleRead)
        val tvSave: TextView = view.findViewById(R.id.tvArticleSave)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val article = getItem(position)
        holder.tvTitle.text = article.title
        holder.tvCategory.text = article.category
        holder.tvTime.text = "⏱ ${article.readMinutes} min"
        holder.tvExcerpt.text = article.excerpt
        holder.tvSave.text = if (article.saved) "🔖" else "🏷️"
        holder.tvRead.setOnClickListener { onRead(article) }
        holder.tvSave.setOnClickListener { onSave(article) }

        // Category color
        val (textColor, bgRes) = when (article.category) {
            "Savings" -> Pair(R.color.income_green, R.drawable.bg_tag_savings)
            "Debt" -> Pair(R.color.expense_red, R.drawable.bg_tag_debt)
            else -> Pair(R.color.blue_primary, R.drawable.bg_tag_learn)
        }
        holder.tvCategory.setTextColor(holder.itemView.context.getColor(textColor))
        holder.tvCategory.setBackgroundResource(bgRes)
    }
}
