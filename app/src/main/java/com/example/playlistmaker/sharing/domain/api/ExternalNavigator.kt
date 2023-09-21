package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {

    fun shareLink(shareAppLink: String)
    fun openLink(termsLink: String)
    fun openEmail(supportEmail: EmailData)


}