package com.protocol.service

import androidx.lifecycle.MutableLiveData
import com.protocol.engine.Engine

/**
 * Движок живёт в сервисе. Экраны читают его отсюда (только чтение, главный поток),
 * а команды шлют через ReminderService, чтобы все изменения шли через сервис.
 */
object EngineHolder {
    @Volatile var engine: Engine? = null
    /** Увеличивается при каждом изменении состояния. */
    val version = MutableLiveData(0)
    /** Последняя ошибка конфига для главного экрана. */
    @Volatile var lastError: String? = null

    fun bump() { version.value = (version.value ?: 0) + 1 }
}
