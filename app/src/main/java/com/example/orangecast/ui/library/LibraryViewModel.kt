package com.example.orangecast.ui.library

import com.example.orangecast.interactor.SubscriptionInteractor
import com.example.orangecast.ui.BaseViewModel

class LibraryViewModel(
    private val subscriptionInteractor: SubscriptionInteractor
): BaseViewModel() {

    fun getAllSubscriptions() {
        subscriptionInteractor.getAllSubscriptions().subscribeWithMapToEvent()
    }
}