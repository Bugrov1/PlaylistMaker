package com.example.playlistmaker.root.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {


private lateinit var binding: ActivityRootBinding
    private lateinit var  navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView( binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
         navController = navHostFragment.navController


        val bottomNavigationView =  binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)



        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.createPlaylistFragment || destination.id ==R.id.playlistScreenFragment||
                destination.id ==R.id.playerFragment||destination.id ==R.id.editPlaylistFragment) {

                bottomNavigationView.visibility = View.GONE
            } else {

                bottomNavigationView.visibility = View.VISIBLE
            }
        }

    }


}