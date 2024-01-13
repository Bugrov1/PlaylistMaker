package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(
    private val context: Context,

    ) : ExternalNavigator {
    override fun shareLink(message: String) {

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        context.startActivity(shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openLink(termsLink: String) {

        val uri = Uri.parse(termsLink)
        val usersIntent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(usersIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openEmail(supportEmail: EmailData) {
        val mailAdress = supportEmail.mailAdress
        val mailTheme = supportEmail.mailTheme
        val mailText = supportEmail.mailText

        val supportIntent = Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(mailAdress))
            putExtra(Intent.EXTRA_SUBJECT, mailTheme)
            putExtra(Intent.EXTRA_TEXT, mailText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }, "chooser")

        context.startActivity(supportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }


}