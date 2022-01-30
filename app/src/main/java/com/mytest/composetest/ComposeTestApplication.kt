package com.mytest.composetest

import android.app.Application
import androidx.annotation.NonNull
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

@HiltAndroidApp
class ComposeTestApplication(override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO) : Application(), CoroutineScope {
    companion object {
        private lateinit var application: ComposeTestApplication
        fun getInstance() = application
    }

    @Override
    override fun onCreate() {
        application = this
        super.onCreate()
    }
}