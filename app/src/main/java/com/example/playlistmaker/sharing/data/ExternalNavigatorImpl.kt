package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(
    private val context: Context,

    ) : ExternalNavigator {
    override fun shareLink(shareAppLink: String) {

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareAppLink)
        context.startActivity(shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openLink(termsLink: String) {

        val uri = Uri.parse(termsLink)
        val usersIntent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(usersIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openEmail(Email: EmailData) {
        val mailAdress = Email.mailAdress
        val mailTheme = Email.mailTheme
        val mailText = Email.mailText
//        val supportIntent = Intent(Intent.ACTION_SENDTO)
//        supportIntent.data = Uri.parse( "mailto:")
//        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailAdress))
//        supportIntent.putExtra(Intent.EXTRA_SUBJECT, mailTheme)
//        supportIntent.putExtra(Intent.EXTRA_TEXT, mailText)
//          context.startActivity(supportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))


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