package com.napominalka.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.napominalka.service.ReminderService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ReminderService.send(context, ReminderService.intent(context, ReminderService.ACTION_TICK))
    }
}
