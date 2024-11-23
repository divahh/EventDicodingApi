package com.example.eventdicoding

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.eventdicoding.data.database.SettingPreferences
import com.example.eventdicoding.data.database.dataStore
import com.example.eventdicoding.databinding.ActivityMainBinding
import com.example.eventdicoding.ui.fragment.FavouriteFragment
import com.example.eventdicoding.ui.fragment.FinishedFragment
import com.example.eventdicoding.ui.fragment.HomeFragment
import com.example.eventdicoding.ui.fragment.UpcomingFragment
import com.example.eventdicoding.viewmodel.SettingViewModel
import com.example.eventdicoding.viewmodel.SettingViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val modeSett = SettingPreferences.getInstance(application.dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(modeSett))[SettingViewModel::class.java]

        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        binding.navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> loadFragment(HomeFragment())
                R.id.navigation_upcoming -> loadFragment(UpcomingFragment())
                R.id.navigation_finished -> loadFragment(FinishedFragment())
                R.id.navigation_favourite -> loadFragment(FavouriteFragment())
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.setting_page -> {
                // Intent ke SettingActivity saat menu Setting diklik
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }
}
