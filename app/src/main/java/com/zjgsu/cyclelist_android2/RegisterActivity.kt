package com.zjgsu.cyclelist_android2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var etRegUsername: EditText
    private lateinit var etRegPassword: EditText
    private lateinit var etRegPhone: EditText
    private lateinit var btnDoRegister: Button
    private lateinit var dbManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 初始化数据库管理器
        dbManager = DatabaseManager(this)

        initViews()
        setListeners()
    }

    private fun initViews() {
        etRegUsername = findViewById(R.id.et_reg_username)
        etRegPassword = findViewById(R.id.et_reg_password)
        etRegPhone = findViewById(R.id.et_reg_phone)
        btnDoRegister = findViewById(R.id.btn_do_register)
    }

    private fun setListeners() {
        btnDoRegister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_do_register -> handleRegister()
        }
    }

    private fun handleRegister() {
        val username = etRegUsername.text.toString().trim()
        val password = etRegPassword.text.toString().trim()
        val phone = etRegPhone.text.toString().trim()

        // 输入验证
        when {
            username.isEmpty() -> {
                Toast.makeText(this, "请设置用户名", Toast.LENGTH_SHORT).show()
                return
            }
            password.isEmpty() -> {
                Toast.makeText(this, "请设置密码", Toast.LENGTH_SHORT).show()
                return
            }
            password.length < 6 -> {
                Toast.makeText(this, "密码长度不能少于6位", Toast.LENGTH_SHORT).show()
                return
            }
            phone.isEmpty() -> {
                Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show()
                return
            }
            phone.length != 11 -> {
                Toast.makeText(this, "请输入有效的11位手机号码", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                // 注册用户
                val userId = dbManager.registerUser(username, password, phone)
                if (userId != -1L) {
                    Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show()
                    finish() // 返回登录界面
                } else {
                    Toast.makeText(this, "注册失败，用户名可能已存在", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        dbManager.close()
        super.onDestroy()
    }
}