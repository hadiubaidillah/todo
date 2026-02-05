package com.hadiubaidillah.platform.notification.listener

interface BaseMessageListener<T> {
    fun onMessage(event: T)
}
