package com.hongja.car

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private val bg = Color.rgb(5, 12, 22)
    private val panel = Color.rgb(13, 27, 45)
    private val cyan = Color.rgb(43, 191, 255)
    private lateinit var clock: TextView
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    private fun shape(color: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color); cornerRadius = 22f; setStroke(2, cyan)
    }
    private fun label(s: String, size: Float = 19f): TextView = TextView(this).apply {
        text = s; textSize = size; setTextColor(Color.WHITE); gravity = Gravity.CENTER
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
    }
    private fun button(s: String, action: () -> Unit): TextView = label(s, 17f).apply {
        background = shape(panel); setPadding(14, 12, 14, 12); setOnClickListener { action() }
    }
    private fun launch(pkg: String) {
        val i = packageManager.getLaunchIntentForPackage(pkg)
        if (i != null) startActivity(i)
        else Toast.makeText(this, "앱이 설치되지 않았어요", Toast.LENGTH_SHORT).show()
    }
    private fun navigation() {
        val names = arrayOf("티맵", "카카오내비")
        android.app.AlertDialog.Builder(this).setTitle("내비게이션 선택")
            .setItems(names) { _, which -> launch(if (which == 0) "com.skt.tmap.ku" else "com.kakao.navi") }.show()
    }
    private fun row(): LinearLayout = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
    private fun col(): LinearLayout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
    private fun add(parent: LinearLayout, child: View, weight: Float) {
        parent.addView(child, LinearLayout.LayoutParams(0, -1, weight).apply { setMargins(5, 5, 5, 5) })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val root = col().apply { setBackgroundColor(bg); setPadding(12, 8, 12, 8) }
        setContentView(root)
        val header = row()
        header.addView(label("🚘  HONGJA CAR  v0.3", 22f), LinearLayout.LayoutParams(0, 60, 1f))
        clock = label("", 18f)
        header.addView(clock, LinearLayout.LayoutParams(0, 60, 1f))
        root.addView(header)
        val content = row()
        root.addView(content, LinearLayout.LayoutParams(-1, 0, 1f))
        val map = col().apply { background = shape(panel); gravity = Gravity.CENTER }
        map.addView(label("NAVIGATION", 30f))
        map.addView(label("실시간 티맵 영역은 기기별 지원 확인 필요", 16f))
        map.addView(button("티맵 / 카카오내비 실행") { navigation() })
        add(content, map, 6f)
        val right = col()
        add(content, right, 4f)
        val timePanel = col().apply { background = shape(panel); gravity = Gravity.CENTER }
        timePanel.addView(label("CLOCK  •  WEATHER", 21f))
        timePanel.addView(label("날씨 연결 준비 중", 16f))
        right.addView(timePanel, LinearLayout.LayoutParams(-1, 0, 1f).apply { setMargins(5,5,5,5) })
        val music = col().apply { background = shape(panel); gravity = Gravity.CENTER }
        music.addView(label("♫  MUSIC", 23f))
        music.addView(label("음악 앱은 설정에서 선택 예정", 15f))
        music.addView(button("음악 앱 선택") {
            val i = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC)
            try { startActivity(Intent.createChooser(i, "음악 앱")) } catch (_: Exception) {
                Toast.makeText(this@MainActivity, "음악 앱을 찾을 수 없어요", Toast.LENGTH_SHORT).show()
            }
        })
        right.addView(music, LinearLayout.LayoutParams(-1, 0, 1f).apply { setMargins(5,5,5,5) })
        val shortcuts = row()
        add(shortcuts, button("NAV") { navigation() }, 1f)
        add(shortcuts, button("APPS") {
            startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
        }, 1f)
        right.addView(shortcuts, LinearLayout.LayoutParams(-1, 74))
        val footer = row()
        listOf("HOME", "NAV", "MUSIC", "SETTINGS").forEach { name ->
            add(footer, button(name) {
                when (name) {
                    "NAV" -> navigation()
                    "SETTINGS" -> startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                    "MUSIC" -> Toast.makeText(this, "음악 패널을 이용해 주세요", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, "홍자 CAR 홈", Toast.LENGTH_SHORT).show()
                }
            }, 1f)
        }
        root.addView(footer, LinearLayout.LayoutParams(-1, 70))
        val tick = object : Runnable {
            override fun run() {
                clock.text = SimpleDateFormat("yyyy.MM.dd  EEE  HH:mm:ss", Locale.KOREA).format(Date())
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(tick)
    }
    override fun onDestroy() { handler.removeCallbacksAndMessages(null); super.onDestroy() }
}
