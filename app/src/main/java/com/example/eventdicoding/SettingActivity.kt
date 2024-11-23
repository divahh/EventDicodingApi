package com.example.eventdicoding

import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.eventdicoding.data.database.SettingPreferences
import com.example.eventdicoding.data.database.dataStore
import com.example.eventdicoding.viewmodel.SettingViewModel
import com.example.eventdicoding.viewmodel.SettingViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.concurrent.TimeUnit

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        supportActionBar?.title = "Setting"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val switchMode = findViewById<SwitchMaterial>(R.id.switch_mode)
        val switchNotif = findViewById<SwitchMaterial>(R.id.switch_notification)

        val modeSett = SettingPreferences.getInstance(application.dataStore)
        val settingViewModel =
            ViewModelProvider(this, SettingViewModelFactory(modeSett))[SettingViewModel::class.java]
        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchMode.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchMode.isChecked = false
            }
        }

        switchMode.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        settingViewModel.getNotificationSettings().observe(this) { isNotificationActive ->
            switchNotif.isChecked = isNotificationActive
        }

        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveNotificationSetting(isChecked)
            if (isChecked) {
                // Start daily reminder if enabled
                startDailyReminder()
            } else {
                // Cancel daily reminder if disabled
                cancelDailyReminder()
            }
        }
    }

    private fun startDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyReminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun cancelDailyReminder() {
        WorkManager.getInstance(this).cancelUniqueWork("DailyReminder")
    }
}