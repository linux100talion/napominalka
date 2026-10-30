package com.napominalka.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.napominalka.config.ConfigStore
import com.napominalka.engine.EngineState
import com.napominalka.engine.Mode
import com.napominalka.service.ReminderService

class TimeChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state = EngineState.fromJson(ConfigStore(context).engineStateJson)
        if (state.mode == Mode.RUNNING || state.procs.isNotEmpty()) {
            ReminderService.send(context, ReminderService.intent(context, ReminderService.ACTION_TIME_CHANGED))
        }
    }
}
