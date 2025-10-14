package com.example.moneytracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.moneytracker.data.worker.NotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MoneyTrackerApp : Application(), Configuration.Provider {

    // 1. Внедрение HiltWorkerFactory (требует Inject)
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // 2. Предоставление конфигурации WorkManager для Hilt (Override val, не fun)
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleDailyReminder()
    }

    // 3. Планирование ежедневного напоминания
    private fun scheduleDailyReminder() {
        val dailyReminderRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            // Устанавливаем задержку до 20:00
            .setInitialDelay(getInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyReminderRequest
        )
    }

    private fun getInitialDelay(): Long {
        val now = Calendar.getInstance()
        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20) // 20:00
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (now.after(dueTime)) {
            // Если сейчас уже после 20:00, запланировать на завтра
            dueTime.add(Calendar.HOUR_OF_DAY, 24)
        }

        return dueTime.timeInMillis - now.timeInMillis
    }
}