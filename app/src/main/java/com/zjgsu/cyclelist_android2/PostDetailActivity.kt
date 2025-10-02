package com.zjgsu.cyclelist_android2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class PostDetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var tvTitle: TextView
    private lateinit var tvAuthor: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvContent: TextView
    private lateinit var tvLikes: TextView
    private lateinit var ivLike: ImageView
    private lateinit var etComment: EditText
    private lateinit var btnSendComment: Button
    private lateinit var llComments: android.widget.LinearLayout
    private lateinit var dbManager: DatabaseManager

    private var postId = 0
    private var isLiked = false
    private var likeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        // 初始化数据库管理器
        dbManager = DatabaseManager(this)

        // 获取传递的帖子ID
        postId = intent.getIntExtra("post_id", 0)

        initViews()
        loadPostData()
        loadComments()
        setListeners()
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tv_post_title)
        tvAuthor = findViewById(R.id.tv_post_author)
        tvTime = findViewById(R.id.tv_post_time)
        tvContent = findViewById(R.id.tv_post_content)
        tvLikes = findViewById(R.id.tv_post_likes)
        ivLike = findViewById(R.id.iv_like)
        etComment = findViewById(R.id.et_comment)
        btnSendComment = findViewById(R.id.btn_send_comment)
        llComments = findViewById(R.id.ll_comments)
        findViewById<View>(R.id.ll_like).setOnClickListener(this)
    }

    private fun loadPostData() {
        val post = dbManager.getPostById(postId)
        post?.let {
            tvTitle.text = it.title
            tvAuthor.text = "作者: ${it.author}"
            tvTime.text = "发布时间: ${it.time}"
            tvContent.text = it.content
            likeCount = it.likes
            tvLikes.text = likeCount.toString()
        } ?: run {
            Toast.makeText(this, "帖子不存在", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadComments() {
        llComments.removeAllViews()
        val comments = dbManager.getCommentsByPostId(postId)

        if (comments.isEmpty()) {
            val tvNoComments = TextView(this).apply {
                text = "还没有评论，快来抢沙发吧！"
                textSize = 14f
                setTextColor(0xFF999999.toInt())
                setPadding(0, 20, 0, 20)
            }
            llComments.addView(tvNoComments)
        } else {
            comments.forEach { comment ->
                val commentView = layoutInflater.inflate(R.layout.item_comment, llComments, false)
                val tvCommentAuthor = commentView.findViewById<TextView>(R.id.tv_comment_author)
                val tvCommentTime = commentView.findViewById<TextView>(R.id.tv_comment_time)
                val tvCommentContent = commentView.findViewById<TextView>(R.id.tv_comment_content)

                tvCommentAuthor.text = comment.author
                tvCommentTime.text = comment.time
                tvCommentContent.text = comment.content

                llComments.addView(commentView)
            }
        }
    }

    private fun setListeners() {
        btnSendComment.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_like -> handleLike()
            R.id.btn_send_comment -> handleSendComment()
        }
    }

    private fun handleLike() {
        isLiked = !isLiked
        if (isLiked) {
            likeCount++
            ivLike.setImageResource(android.R.drawable.btn_star_big_on)
            Toast.makeText(this, "点赞成功", Toast.LENGTH_SHORT).show()
        } else {
            likeCount--
            ivLike.setImageResource(android.R.drawable.btn_star_big_off)
            Toast.makeText(this, "取消点赞", Toast.LENGTH_SHORT).show()
        }
        tvLikes.text = likeCount.toString()

        // 更新数据库中的点赞数
        dbManager.updatePostLikes(postId, likeCount)
    }

    private fun handleSendComment() {
        val commentContent = etComment.text.toString().trim()
        if (commentContent.isEmpty()) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show()
            return
        }

        if (CurrentUser.username.isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
            return
        }

        // 获取当前时间
        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())

        // 创建评论对象
        val comment = Comment(
            postId = postId,
            author = CurrentUser.username,
            content = commentContent,
            time = currentTime
        )

        // 保存评论到数据库
        val commentId = dbManager.addComment(comment)
        if (commentId != -1L) {
            etComment.text.clear()
            loadComments() // 重新加载评论列表
            Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "评论失败", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        dbManager.close()
        super.onDestroy()
    }
}