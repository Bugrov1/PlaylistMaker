package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return "https://practicum.yandex.ru/profile/android-developer/"
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            mailAdress = "px316@yandex.ru",
            mailTheme = "Сообщение разработчикам и разработчицам приложения Playlist Maker",
            mailText = "Спасибо разработчикам и разработчицам за крутое приложение!"
        )
    }

    private fun getTermsLink(): String {
        return "https://yandex.ru/legal/practicum_offer/"
    }
}
