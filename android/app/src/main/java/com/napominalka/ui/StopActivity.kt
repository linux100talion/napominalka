package com.napominalka.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.napominalka.databinding.ActivityStopBinding

/** «Программа остановлена. Исправьте конфиг и запустите снова.» SPEC 5.5. */
class StopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityStopBinding.inflate(layoutInflater)
        setContentView(b.root)
        if (android.os.Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
        b.okButton.setOnClickListener { finish() }
    }
}
