package com.napominalka.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.napominalka.receiver.AlarmReceiver
import com.napominalka.ui.MainActivity

/**
 * Один будильник на ближайший такт движка. setAlarmClock не подавляется Doze. SPEC 8.
 */
object AlarmScheduler {
    private const val REQ = 42

    fun schedule(ctx: Context, atMillis: Long) {
        val am = ctx.getSystemService(AlarmManager::class.java)
        val show = PendingIntent.getActivity(ctx, 1, Intent(ctx, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        am.setAlarmClock(AlarmManager.AlarmClockInfo(atMillis, show), pending(ctx))
    }

    fun cancel(ctx: Context) {
        ctx.getSystemService(AlarmManager::class.java).cancel(pending(ctx))
    }

    private fun pending(ctx: Context): PendingIntent =
        PendingIntent.getBroadcast(ctx, REQ, Intent(ctx, AlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
}
