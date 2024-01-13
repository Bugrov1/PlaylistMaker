package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {

    fun shareLink(message: String)
    fun openLink(termsLink: String)
    fun openEmail(supportEmail: EmailData)


}