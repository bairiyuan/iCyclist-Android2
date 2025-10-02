package com.zjgsu.cyclelist_android2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var dbManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 初始化数据库管理器
        dbManager = DatabaseManager(this)

        initViews()
        setListeners()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_register)
    }

    private fun setListeners() {
        btnLogin.setOnClickListener(this)
        btnRegister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> handleLogin()
            R.id.btn_register -> navigateToRegister()
        }
    }

    private fun handleLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // 输入验证
        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
            return
        }

        // 数据库验证
        val loginSuccess = dbManager.loginUser(username, password)
        if (loginSuccess) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
            // 保存当前用户信息
            CurrentUser.username = username
            // 登录成功跳转到主界面
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    override fun onDestroy() {
        dbManager.close()
        super.onDestroy()
    }
}

// 当前用户信息
object CurrentUser {
    var username: String = ""
    var phone: String = ""
}