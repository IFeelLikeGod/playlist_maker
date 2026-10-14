package com.example.playlist_maker.domain.sharing.navigator

import com.example.playlist_maker.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(email: EmailData)
}