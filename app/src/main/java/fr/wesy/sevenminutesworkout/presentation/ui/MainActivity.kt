package fr.wesy.sevenminutesworkout.presentation.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import fr.wesy.sevenminutesworkout.R
import fr.wesy.sevenminutesworkout.data.NetworkStatusObserver
import fr.wesy.sevenminutesworkout.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var navView: BottomNavigationView? = null
    private var drawerLayout: DrawerLayout? = null
    private lateinit var navController: NavController

    @Inject
    lateinit var networkStatusObserver: NetworkStatusObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (resources.getBoolean(R.bool.is_portrait_only)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        navView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        binding.navigationView?.setupWithNavController(navController)

        if (navView != null) {
            // Menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_workouts, R.id.navigation_meditation, R.id.navigation_information
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView?.setupWithNavController(navController)
        }

        drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)?.apply {
            val navigationView: NavigationView? = findViewById(R.id.navigationView)
            navigationView?.setupWithNavController(navController)
            findViewById<ImageButton>(R.id.hamburgerMenuButton)?.setOnClickListener {
                openDrawer(GravityCompat.START)
            }
        }

        setupNavigationView()

        handleDrawerBackPressed()
    }

    fun showNoConnectionDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.no_connection_title)
            .setMessage(R.string.no_connection_message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun setupNavigationView() {
        binding.navigationView?.apply {
            setupWithNavController(navController)
            setNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_workouts -> navController.navigate(R.id.navigation_workouts)
                    R.id.navigation_meditation -> navController.navigate(R.id.navigation_meditation)
                    R.id.navigation_information -> navController.navigate(R.id.navigation_information)
                    else -> return@setNavigationItemSelectedListener false
                }
                binding.drawerLayout?.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    private fun handleDrawerBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
                    drawerLayout?.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    this@MainActivity.onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    fun setHamburgerButtonVisibility(visible: Boolean) {
        binding.hamburgerMenuButton?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setBottomNavVisibility(visible: Boolean) {
        navView?.visibility = if (visible) View.VISIBLE else View.GONE
    }
}