package com.zjgsu.cyclelist_android2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class UserFragment : Fragment() {
    private lateinit var tvUsername: TextView
    private lateinit var tvPhone: TextView
    private lateinit var btnLogout: Button
    private lateinit var dbManager: DatabaseManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        // 初始化数据库管理器
        dbManager = DatabaseManager(requireContext())

        initViews(view)
        setListeners()
        updateUserInfo()
        return view
    }

    private fun initViews(view: View) {
        tvUsername = view.findViewById(R.id.tv_username)
        tvPhone = view.findViewById(R.id.tv_phone)
        btnLogout = view.findViewById(R.id.btn_logout)
    }

    private fun setListeners() {
        btnLogout.setOnClickListener {
            // 显示退出登录提示
            Toast.makeText(requireContext(), "退出登录", Toast.LENGTH_SHORT).show()
            // 调用MainActivity的退出方法
            (activity as? MainActivity)?.logout()
        }
    }

    private fun updateUserInfo() {
        if (CurrentUser.username.isNotEmpty()) {
            val user = dbManager.getUserByUsername(CurrentUser.username)
            user?.let {
                tvUsername.text = it.username
                tvPhone.text = it.phone
                CurrentUser.phone = it.phone
            }
        } else {
            tvUsername.text = "未登录"
            tvPhone.text = "未绑定"
        }
    }

    override fun onDestroy() {
        dbManager.close()
        super.onDestroy()
    }
}