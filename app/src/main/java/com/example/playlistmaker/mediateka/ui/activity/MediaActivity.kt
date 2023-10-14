package com.example.playlistmaker.mediateka.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.mediateka.ui.MediaViewPagerAdapter
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding

    private lateinit var tabMediator: TabLayoutMediator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager.adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle)
        setListeners()



        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = "Избранные треки"
                1 -> tab.text = "Плейлисты"

            }
        }
        tabMediator.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }

    fun setListeners() {
        binding .backButton.setOnClickListener {
            finish()

        }

    }
}