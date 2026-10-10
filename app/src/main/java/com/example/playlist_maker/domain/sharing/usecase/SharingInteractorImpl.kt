package com.example.playlist_maker.domain.sharing.usecase

import com.example.playlist_maker.domain.models.EmailData
import com.example.playlist_maker.domain.sharing.navigator.ExternalNavigator

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp() = externalNavigator.shareLink(getShareAppLink())
    override fun openTerms() = externalNavigator.openLink(getTermsLink())
    override fun openSupport() = externalNavigator.openEmail(getSupportEmailData())

    private fun getShareAppLink(): String = "https://practicum.yandex.ru/android-developer/"

    private fun getTermsLink(): String = "https://yandex.ru/legal/practicum_offer/"

    private fun getSupportEmailData() = EmailData(
        email = "student@example.com",
        subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker",
        text = "Спасибо разработчикам и разработчицам за крутое приложение!"
    )
}