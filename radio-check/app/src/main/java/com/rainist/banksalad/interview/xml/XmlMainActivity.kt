package com.rainist.banksalad.interview.xml

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import com.rainist.banksalad.interview.R

/**
 * 유선 면접 테스트의 구현을 진행할 기본 액티비티 입니다. (xml 세팅)
 */
class XmlMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.textLogger).movementMethod = ScrollingMovementMethod()
    }
}
