package info.froydenzi.trovotrick.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import info.froydenzi.trovotrick.R


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    @SuppressLint("RtlHardcoded")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val menuButton: ImageView = findViewById(R.id.menu_icon)
        val logoutButton: TextView = findViewById(R.id.logout_item)
        val settings = getSharedPreferences("registration", 0)

        //Navigation Drawer
        navController = findNavController(R.id.fragment)
        drawerLayout = findViewById(R.id.activity_main)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        val navView: NavigationView = findViewById(R.id.navigationView)
        val loginItem = navView.menu.findItem(R.id.loginFragment)
        val registerItem = navView.menu.findItem(R.id.signUpFragment)

        //Inflating nav_header layout so i can change some text
        val header = navView.getHeaderView(0)
        val fullNameNav = header.findViewById<TextView>(R.id.fullname_navigation)
        val emailNav = header.findViewById<TextView>(R.id.email_navigation)

        navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        logoutButton.setOnClickListener {
            settings.edit().remove("username").apply()
            settings.edit().remove("email").apply()
            settings.edit().remove("fullname").apply()
            settings.edit().putBoolean("status", false).apply()
            settings.edit().putBoolean("approved", false).apply()

            loginItem.isVisible = true
            registerItem.isVisible = true
            logoutButton.visibility = View.GONE

            startActivity(Intent(this, MainActivity::class.java))
        }

        if ((settings.contains("username")) && (settings.contains("email"))) {
            logoutButton.visibility = View.VISIBLE
            loginItem.isVisible = false
            registerItem.isVisible = false
            fullNameNav.text = settings.getString("fullname", null)
            emailNav.text = settings.getString("email", null)
            Log.i("START-UP TROVO APP", "Ima i username i email,znaci logovan!")
        } else {
            loginItem.isVisible = true
            registerItem.isVisible = true
            logoutButton.visibility = View.GONE
        }

        if (settings.getBoolean("firstTime", true)) {
            val bluetoothName = Settings.Secure.getString(contentResolver, "bluetooth_name")
            val serialNumber = Build.MODEL + "/" + bluetoothName

            Log.w("START-UP TROVO APP", "Treba biti prikazano samo jednom!")

            settings.edit().putString("device", serialNumber).apply()
            settings.edit().putBoolean("firstTime", false).apply()
            settings.edit().putBoolean("approved", false).apply()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}



