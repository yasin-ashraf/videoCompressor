package com.yasin.trellvideo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.yasin.trellvideo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var activityMainBinding: ActivityMainBinding
  private lateinit var navController : NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(activityMainBinding.root)
    init()
  }

  private fun init() {
    val navHostFragment: NavHostFragment =
      supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
    navController = navHostFragment.navController
  }

  override fun onNavigateUp(): Boolean {
    return navController.navigateUp() || super.onNavigateUp()
  }
}
