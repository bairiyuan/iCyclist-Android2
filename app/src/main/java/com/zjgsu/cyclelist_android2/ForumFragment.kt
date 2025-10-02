package com.zjgsu.cyclelist_android2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ForumFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var dbManager: DatabaseManager
    private val postList = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_forum, container, false)

        // 初始化数据库管理器
        dbManager = DatabaseManager(requireContext())

        initView(view)
        initData()
        initTabs(view)
        return view
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.rv_posts)
        recyclerView.layoutManager = LinearLayoutManager(context)
        postAdapter = PostAdapter(postList) { post ->
            // 点击帖子跳转到详情页
            val intent = Intent(context, PostDetailActivity::class.java)
            intent.putExtra("post_id", post.id)
            startActivity(intent)
        }
        recyclerView.adapter = postAdapter
    }

    private fun initData() {
        // 从数据库获取帖子数据
        val posts = dbManager.getAllPosts()
        postList.clear()
        postList.addAll(posts)
        postAdapter.notifyDataSetChanged()
    }

    private fun initTabs(view: View) {
        // 论坛分类标签点击事件
        val tabs = listOf(
            R.id.tv_equipment, R.id.tv_secondhand, R.id.tv_hiking,
            R.id.tv_running, R.id.tv_live, R.id.tv_driving, R.id.tv_qa
        )

        tabs.forEach { tabId ->
            view.findViewById<TextView>(tabId).setOnClickListener {
                // 重置所有标签颜色
                tabs.forEach { id ->
                    view.findViewById<TextView>(id).setTextColor(
                        resources.getColor(android.R.color.darker_gray, null)
                    )
                }
                // 设置当前标签颜色
                (it as TextView).setTextColor(
                    resources.getColor(android.R.color.holo_blue_dark, null)
                )

                // 根据分类筛选帖子
                val categoryText = (it as TextView).text.toString()
                val filteredPosts = if (categoryText == "全部") {
                    dbManager.getAllPosts()
                } else {
                    dbManager.getAllPosts(categoryText)
                }

                postList.clear()
                postList.addAll(filteredPosts)
                postAdapter.notifyDataSetChanged()

                Toast.makeText(context, "切换到: $categoryText", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        dbManager.close()
        super.onDestroy()
    }
}