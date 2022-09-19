package com.ottogo.weekly.authenticator

import android.app.Service
import android.content.Intent
import android.os.IBinder


class AuthenticationService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        val authenticator = Authenticator(this)
        return authenticator.getIBinder()
    }
}