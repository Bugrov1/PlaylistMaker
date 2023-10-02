package com.example.playlistmaker.DI

import com.example.playlistmaker.main.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {


    viewModel<MainViewModel> {
        MainViewModel(
        )
    }

}