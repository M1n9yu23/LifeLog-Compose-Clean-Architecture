package com.bossmg.android.notifications

interface NotificationSender {
    fun send(title: String, message: String)
}