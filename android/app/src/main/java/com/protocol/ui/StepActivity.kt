package com.protocol.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.protocol.R
import com.protocol.databinding.ActivityStepBinding
import com.protocol.engine.Mode
import com.protocol.engine.Screen
import com.protocol.service.EngineHolder
import com.protocol.service.ReminderService

/**
 * Экран шага или процесса. Рисует Screen из движка. SPEC 6.2.
 * Назад не работает. Закрывается сам, когда показывать нечего.
 */
class StepActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PROC_ID = "proc_id"
    }

    private lateinit var b: ActivityStepBinding
    private var procId: String? = null
    private var wasStepReason = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityStepBinding.inflate(layoutInflater)
        setContentView(b.root)
        if (android.os.Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        procId = intent.getStringExtra(EXTRA_PROC_ID)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { /* назад запрещён */ }
        })
        EngineHolder.version.observe(this) { render() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        procId = intent.getStringExtra(EXTRA_PROC_ID)
        render()
    }

    private fun render() {
        val engine = EngineHolder.engine ?: run { finishAndMaybeShowStopped(); return }
        // Шаг в приоритете. Иначе процесс из интента, иначе любой активный процесс.
        var target: String? = null
        var screen: Screen? = engine.stepScreen()
        if (screen == null) {
            target = procId ?: engine.state.procs.firstOrNull()?.procId
            screen = target?.let { engine.procScreen(it) }
        }
        val scr = screen ?: run { finishAndMaybeShowStopped(); return }
        if (scr is Screen.Reason && scr.isStep) wasStepReason = true

        b.titleText.text = scr.title
        b.questionText.text = scr.text
        b.buttons.removeAllViews()
        b.answerInput.visibility = android.view.View.GONE
        b.answerInput.setText("")

        when (scr) {
            is Screen.YesNo -> {
                addButton(getString(R.string.btn_yes)) { ReminderService.answer(this, target, "yes") }
                addButton(getString(R.string.btn_no)) { ReminderService.answer(this, target, "no") }
            }
            is Screen.Ack -> addButton(getString(R.string.btn_ack)) { ReminderService.answer(this, target, "ack") }
            is Screen.Text -> {
                b.answerInput.visibility = android.view.View.VISIBLE
                addButton(getString(R.string.btn_answer)) {
                    val v = b.answerInput.text.toString().trim()
                    if (v.isEmpty()) Toast.makeText(this, R.string.text_empty, Toast.LENGTH_SHORT).show()
                    else ReminderService.answer(this, target, "text", v)
                }
            }
            is Screen.Choice -> scr.options.forEachIndexed { i, label ->
                addButton(label) { ReminderService.answer(this, target, "choice", i.toString()) }
            }
            is Screen.Wait -> addButton(scr.button) { ReminderService.answer(this, target, "resume") }
            is Screen.Reason -> {
                b.answerInput.visibility = android.view.View.VISIBLE
                b.answerInput.hint = getString(R.string.stop_hint)
                addButton(getString(R.string.btn_answer)) {
                    val v = b.answerInput.text.toString().trim()
                    if (v.isEmpty()) Toast.makeText(this, R.string.stop_empty, Toast.LENGTH_SHORT).show()
                    else ReminderService.send(this, ReminderService.intent(this, ReminderService.ACTION_REASON)
                        .putExtra(ReminderService.EXTRA_IS_STEP, scr.isStep)
                        .putExtra(ReminderService.EXTRA_ID, scr.id)
                        .putExtra(ReminderService.EXTRA_VALUE, v))
                }
            }
        }
    }

    private fun addButton(label: String, onClick: () -> Unit) {
        b.buttons.addView(Button(this).apply {
            text = label
            textSize = 22f
            minHeight = 160
            setOnClickListener { onClick() }
        })
    }

    private fun finishAndMaybeShowStopped() {
        if (wasStepReason && EngineHolder.engine?.state?.mode == Mode.STOPPED) {
            startActivity(Intent(this, StopActivity::class.java))
        }
        finish()
    }
}
