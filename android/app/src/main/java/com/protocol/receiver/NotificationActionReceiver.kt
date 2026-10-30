package com.protocol.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.protocol.service.ReminderService

/** Кнопки «Да», «Нет» и кнопка узла wait в уведомлениях. */
class NotificationActionReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_YES = "com.protocol.YES"
        const val ACTION_NO = "com.protocol.NO"
        const val ACTION_RESUME = "com.protocol.RESUME"
        const val EXTRA_PROC_ID = "proc_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val procId = intent.getStringExtra(EXTRA_PROC_ID)
        val kind = when (intent.action) {
            ACTION_YES -> "yes"
            ACTION_NO -> "no"
            ACTION_RESUME -> "resume"
            else -> return
        }
        ReminderService.answer(context, procId, kind)
    }
}
