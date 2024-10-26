package com.example.psychika.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.psychika.R
import com.example.psychika.data.entity.Article

class ArticleAdapter(private var listArticle: ArrayList<Article>) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallBack

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallback = onItemClickCallBack
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPublisher: TextView = itemView.findViewById(R.id.tv_publisher)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val ivPhoto: ImageView = itemView.findViewById(R.id.iv_article_picture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_article, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listArticle.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (_, publisher, title, date, photo) = listArticle[position]
        holder.apply {
            tvPublisher.text = publisher
            tvTitle.text = title
            tvDate.text = date
            ivPhoto.setImageResource(photo)

            itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(listArticle[holder.adapterPosition])
            }
        }
    }

    fun setFilteredList(listArticle: ArrayList<Article>) {
        this.listArticle = listArticle
        notifyDataSetChanged()
    }

    interface OnItemClickCallBack {
        fun onItemClicked(data: Article)
    }
}