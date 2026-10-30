package com.protocol.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.protocol.config.ConfigStore
import com.protocol.engine.EngineState
import com.protocol.engine.Mode
import com.protocol.log.EventLog
import com.protocol.service.ReminderService
import java.time.LocalDate

/**
 * После перезагрузки: если программа работала или есть открытые процессы,
 * поднять сервис. Остановленная программа сама не стартует (SPEC 5.5, 5.7).
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val store = ConfigStore(context)
        val state = EngineState.fromJson(store.engineStateJson)
        EventLog(context).write(LocalDate.now(), "BOOT", "mode=${state.mode}")
        if (state.mode == Mode.RUNNING || state.procs.isNotEmpty()) {
            ReminderService.send(context, ReminderService.intent(context, ReminderService.ACTION_TICK))
        }
    }
}
