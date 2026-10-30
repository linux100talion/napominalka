package com.protocol.engine

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/** Расчёт циклов. SPEC.md, разделы 4.4 и 5.6. */
object Schedule {
    const val DAY_MS = 24 * 60 * 60 * 1000L

    /** Начало текущего цикла: последний day_start, который уже наступил. */
    fun cycleStartFor(nowMs: Long, dayStartMin: Int, zone: ZoneId = ZoneId.systemDefault()): Long {
        val now = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nowMs), zone)
        val startTime = LocalTime.of(dayStartMin / 60, dayStartMin % 60)
        var start = ZonedDateTime.of(now.toLocalDate(), startTime, zone)
        if (start.isAfter(now)) start = ZonedDateTime.of(now.toLocalDate().minusDays(1), startTime, zone)
        return start.toInstant().toEpochMilli()
    }

    /** Следующее начало цикла после данного. Учитывает переход на летнее время. */
    fun nextCycleStart(cycleStartMs: Long, dayStartMin: Int, zone: ZoneId = ZoneId.systemDefault()): Long {
        val start = ZonedDateTime.ofInstant(Instant.ofEpochMilli(cycleStartMs), zone)
        val startTime = LocalTime.of(dayStartMin / 60, dayStartMin % 60)
        return ZonedDateTime.of(start.toLocalDate().plusDays(1), startTime, zone).toInstant().toEpochMilli()
    }

    /** Плановое время шага внутри цикла. */
    fun dueAt(cycleStartMs: Long, offsetMin: Int): Long = cycleStartMs + offsetMin * 60_000L

    fun cycleDate(cycleStartMs: Long, zone: ZoneId = ZoneId.systemDefault()): LocalDate =
        Instant.ofEpochMilli(cycleStartMs).atZone(zone).toLocalDate()
}
