package com.training.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.training.foodrunner.*
import com.training.foodrunner.fragment.*

class Home : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var sharedPreferences: SharedPreferences
    private var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_home)

        toolbar = findViewById(R.id.tb_home)
        drawerLayout = findViewById(R.id.drawer_layout)
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        frameLayout = findViewById(R.id.frame_layout)
        navigationView = findViewById(R.id.navigation_layout)
        sharedPreferences.getBoolean("isLoggedInTroughLogin", false)
        val username = sharedPreferences.getString("name", "User Name")
        val headerView = navigationView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.header_username).text = username
        setActionBar()

        /*if (intent.getStringExtra("data") == "register") {
            toolbar.title = intent.getStringExtra("firstName")
        }*/

        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@Home, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.it_drawer_home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.it_drawer_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame_layout,
                            ProfileFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.it_drawer_favorites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame_layout,
                            FavoritesFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Favorites"
                    drawerLayout.closeDrawers()
                }
                R.id.it_drawer_order_history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame_layout,
                            OrderHistoryFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.it_drawer_faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame_layout,
                            FaqsFragment()
                        )
                        .commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun setActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.it_log_out) {
            sharedPreferences.edit().clear().apply()
            finish()
            val logOutIntent = Intent(this@Home, LoginActivity::class.java)
            startActivity(logOutIntent)
            Toast.makeText(this@Home, "Logged out", Toast.LENGTH_SHORT).show()
        }

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openHome() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()
        navigationView.setCheckedItem(R.id.it_drawer_home)
        supportActionBar?.title = "Home"
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frame_layout)) {
            !is HomeFragment -> openHome()
            else -> {
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(a)
                super.onBackPressed()
            }
        }
    }
}