package ru.alexeypodusov.sample649.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.alexeypodusov.sample649.R

class TabsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs)

        findViewById<BottomNavigationView>(R.id.bottom_navigation_view).apply {
            setupWithNavController(
                (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                    .navController
            )
        }

        findViewById<Button>(R.id.compose_button).setOnClickListener {
            startActivity(
                Intent(this, ComposeTabsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}