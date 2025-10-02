package com.zjgsu.cyclelist_android2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(
    private val postList: List<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAuthor: TextView = view.findViewById(R.id.tv_author)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvContent: TextView = view.findViewById(R.id.tv_content)
        val tvLikes: TextView = view.findViewById(R.id.tv_likes)
        val tvComments: TextView = view.findViewById(R.id.tv_comments)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(postList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]
        holder.tvAuthor.text = post.author
        holder.tvTime.text = post.time
        holder.tvTitle.text = post.title
        holder.tvContent.text = post.content
        holder.tvLikes.text = post.likes.toString()
        holder.tvComments.text = post.comments.toString()
    }

    override fun getItemCount() = postList.size
}