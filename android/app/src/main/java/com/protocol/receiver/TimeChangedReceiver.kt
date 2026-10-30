package com.protocol.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.protocol.config.ConfigStore
import com.protocol.engine.EngineState
import com.protocol.engine.Mode
import com.protocol.service.ReminderService

class TimeChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state = EngineState.fromJson(ConfigStore(context).engineStateJson)
        if (state.mode == Mode.RUNNING || state.procs.isNotEmpty()) {
            ReminderService.send(context, ReminderService.intent(context, ReminderService.ACTION_TIME_CHANGED))
        }
    }
}
