package com.zjgsu.cyclelist_android2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class DatabaseManager(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // 用户相关操作
    fun registerUser(username: String, password: String, phone: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USERNAME, username)
            put(DatabaseHelper.COLUMN_PASSWORD, password)
            put(DatabaseHelper.COLUMN_PHONE, phone)
        }
        return db.insert(DatabaseHelper.TABLE_USERS, null, values)
    }

    fun loginUser(username: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COLUMN_USERNAME} = ? AND ${DatabaseHelper.COLUMN_PASSWORD} = ?",
            arrayOf(username, password),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getUserByUsername(username: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COLUMN_USERNAME} = ?",
            arrayOf(username),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) {
                User(
                    id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                    username = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)),
                    password = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD)),
                    phone = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE))
                )
            } else {
                null
            }
        }
    }

    // 帖子相关操作
    fun getAllPosts(category: String? = null): List<Post> {
        val db = dbHelper.readableDatabase
        val selection = if (category.isNullOrEmpty()) null else "${DatabaseHelper.COLUMN_POST_CATEGORY} = ?"
        val selectionArgs = if (category.isNullOrEmpty()) null else arrayOf(category)

        val cursor = db.query(
            DatabaseHelper.TABLE_POSTS,
            null,
            selection,
            selectionArgs,
            null, null,
            "${DatabaseHelper.COLUMN_POST_TIME} DESC"
        )

        return cursor.use {
            val posts = mutableListOf<Post>()
            while (it.moveToNext()) {
                posts.add(
                    Post(
                        id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_TITLE)),
                        content = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_CONTENT)),
                        author = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_AUTHOR)),
                        time = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_TIME)),
                        category = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_CATEGORY)),
                        likes = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_LIKES))
                    )
                )
            }
            posts
        }
    }

    fun getPostById(postId: Int): Post? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_POSTS,
            null,
            "${DatabaseHelper.COLUMN_POST_ID} = ?",
            arrayOf(postId.toString()),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) {
                Post(
                    id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_TITLE)),
                    content = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_CONTENT)),
                    author = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_AUTHOR)),
                    time = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_TIME)),
                    category = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_CATEGORY)),
                    likes = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POST_LIKES))
                )
            } else {
                null
            }
        }
    }

    fun addPost(post: Post): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_POST_TITLE, post.title)
            put(DatabaseHelper.COLUMN_POST_CONTENT, post.content)
            put(DatabaseHelper.COLUMN_POST_AUTHOR, post.author)
            put(DatabaseHelper.COLUMN_POST_TIME, post.time)
            put(DatabaseHelper.COLUMN_POST_CATEGORY, post.category)
            put(DatabaseHelper.COLUMN_POST_LIKES, post.likes)
        }
        return db.insert(DatabaseHelper.TABLE_POSTS, null, values)
    }

    fun updatePostLikes(postId: Int, newLikes: Int): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_POST_LIKES, newLikes)
        }
        val rowsAffected = db.update(
            DatabaseHelper.TABLE_POSTS,
            values,
            "${DatabaseHelper.COLUMN_POST_ID} = ?",
            arrayOf(postId.toString())
        )
        return rowsAffected > 0
    }

    // 评论相关操作
    fun getCommentsByPostId(postId: Int): List<Comment> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_COMMENTS,
            null,
            "${DatabaseHelper.COLUMN_COMMENT_POST_ID} = ?",
            arrayOf(postId.toString()),
            null, null,
            "${DatabaseHelper.COLUMN_COMMENT_TIME} ASC"
        )

        return cursor.use {
            val comments = mutableListOf<Comment>()
            while (it.moveToNext()) {
                comments.add(
                    Comment(
                        id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMMENT_ID)),
                        postId = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMMENT_POST_ID)),
                        author = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMMENT_AUTHOR)),
                        content = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMMENT_CONTENT)),
                        time = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMMENT_TIME))
                    )
                )
            }
            comments
        }
    }

    fun addComment(comment: Comment): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_COMMENT_POST_ID, comment.postId)
            put(DatabaseHelper.COLUMN_COMMENT_AUTHOR, comment.author)
            put(DatabaseHelper.COLUMN_COMMENT_CONTENT, comment.content)
            put(DatabaseHelper.COLUMN_COMMENT_TIME, comment.time)
        }
        return db.insert(DatabaseHelper.TABLE_COMMENTS, null, values)
    }

    fun close() {
        dbHelper.close()
    }
}