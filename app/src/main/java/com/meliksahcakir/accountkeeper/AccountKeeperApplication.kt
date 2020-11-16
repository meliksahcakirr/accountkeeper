package com.meliksahcakir.accountkeeper

import android.app.Application
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import com.chibatching.kotpref.Kotpref
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.utils.Preferences
import com.meliksahcakir.accountkeeper.utils.ServiceLocator
import timber.log.Timber

class AccountKeeperApplication : Application() {

    companion object {
        var unhandledDeepLink: Uri? = null
    }

    lateinit var accountRepository: AccountRepository

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        accountRepository = ServiceLocator.provideAccountRepository(this)
        Timber.plant(Timber.DebugTree())
        AppCompatDelegate.setDefaultNightMode(Preferences.nightMode)
    }
}