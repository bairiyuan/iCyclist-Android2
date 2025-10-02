package com.zjgsu.cyclelist_android2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cycling_club.db"
        private const val DATABASE_VERSION = 1

        // 表名
        const val TABLE_USERS = "users"
        const val TABLE_POSTS = "posts"
        const val TABLE_COMMENTS = "comments"

        // 用户表字段
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_PHONE = "phone"

        // 帖子表字段
        const val COLUMN_POST_ID = "id"
        const val COLUMN_POST_TITLE = "title"
        const val COLUMN_POST_CONTENT = "content"
        const val COLUMN_POST_AUTHOR = "author"
        const val COLUMN_POST_TIME = "time"
        const val COLUMN_POST_CATEGORY = "category"
        const val COLUMN_POST_LIKES = "likes"

        // 评论表字段
        const val COLUMN_COMMENT_ID = "id"
        const val COLUMN_COMMENT_POST_ID = "post_id"
        const val COLUMN_COMMENT_AUTHOR = "author"
        const val COLUMN_COMMENT_CONTENT = "content"
        const val COLUMN_COMMENT_TIME = "time"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 创建用户表
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createUsersTable)

        // 创建帖子表
        val createPostsTable = """
            CREATE TABLE $TABLE_POSTS (
                $COLUMN_POST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_POST_TITLE TEXT NOT NULL,
                $COLUMN_POST_CONTENT TEXT NOT NULL,
                $COLUMN_POST_AUTHOR TEXT NOT NULL,
                $COLUMN_POST_TIME TEXT NOT NULL,
                $COLUMN_POST_CATEGORY TEXT NOT NULL,
                $COLUMN_POST_LIKES INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createPostsTable)

        // 创建评论表
        val createCommentsTable = """
            CREATE TABLE $TABLE_COMMENTS (
                $COLUMN_COMMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_COMMENT_POST_ID INTEGER NOT NULL,
                $COLUMN_COMMENT_AUTHOR TEXT NOT NULL,
                $COLUMN_COMMENT_CONTENT TEXT NOT NULL,
                $COLUMN_COMMENT_TIME TEXT NOT NULL,
                FOREIGN KEY ($COLUMN_COMMENT_POST_ID) REFERENCES $TABLE_POSTS($COLUMN_POST_ID)
            )
        """.trimIndent()
        db.execSQL(createCommentsTable)

        // 插入测试数据
        insertTestData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMMENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_POSTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun insertTestData(db: SQLiteDatabase) {
        // 插入测试用户
        val userValues = ContentValues().apply {
            put(COLUMN_USERNAME, "test")
            put(COLUMN_PASSWORD, "123456")
            put(COLUMN_PHONE, "13800138000")
        }
        db.insert(TABLE_USERS, null, userValues)

        // 插入测试帖子
        val categories = listOf("装备讨论", "二手交易", "徒步户外", "跑步越野", "运动直播", "自驾摩旅", "有问必答")
        categories.forEachIndexed { index, category ->
            val postValues = ContentValues().apply {
                put(COLUMN_POST_TITLE, "$category 的精彩分享 #${index + 1}")
                put(COLUMN_POST_CONTENT, "这是一篇关于$category 的精彩内容，欢迎大家一起来讨论交流！")
                put(COLUMN_POST_AUTHOR, "骑友${index + 1}")
                put(COLUMN_POST_TIME, "${index + 1}小时前")
                put(COLUMN_POST_CATEGORY, category)
                put(COLUMN_POST_LIKES, (10 + index * 3))
            }
            db.insert(TABLE_POSTS, null, postValues)
        }
    }
}