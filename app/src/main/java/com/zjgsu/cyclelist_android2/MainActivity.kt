package com.zjgsu.cyclelist_android2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(), View.OnClickListener {

    // 碎片实例
    private val forumFragment = ForumFragment()
    private val userFragment = UserFragment()
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化点击事件
        initListeners()

        // 默认显示论坛碎片
        switchFragment(forumFragment)

        // 设置默认选中状态
        updateBottomNavState(R.id.ll_forum)
    }

    private fun initListeners() {
        findViewById<LinearLayout>(R.id.ll_forum).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.ll_user).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_forum -> {
                updateBottomNavState(R.id.ll_forum)
                switchFragment(forumFragment)
            }
            R.id.ll_user -> {
                updateBottomNavState(R.id.ll_user)
                switchFragment(userFragment)
            }
        }
    }

    /**
     * 切换碎片
     */
    private fun switchFragment(targetFragment: Fragment) {
        if (targetFragment == currentFragment) return

        val transaction = supportFragmentManager.beginTransaction()

        // 隐藏当前显示的碎片
        currentFragment?.let { transaction.hide(it) }

        // 显示目标碎片（未添加则添加）
        if (!targetFragment.isAdded) {
            transaction.add(R.id.fl_content, targetFragment, targetFragment::class.java.simpleName)
        } else {
            transaction.show(targetFragment)
        }

        transaction.commit()
        currentFragment = targetFragment
    }

    /**
     * 更新底部导航状态
     */
    private fun updateBottomNavState(selectedId: Int) {
        // 这里可以添加选中状态的UI变化，比如改变颜色等
        when (selectedId) {
            R.id.ll_forum -> {
                // 论坛选中状态
                Toast.makeText(this, "切换到论坛", Toast.LENGTH_SHORT).show()
            }
            R.id.ll_user -> {
                // 用户选中状态
                Toast.makeText(this, "切换到个人中心", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 退出登录跳转回登录界面
     */
    fun logout() {
        Toast.makeText(this, "退出登录成功", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}