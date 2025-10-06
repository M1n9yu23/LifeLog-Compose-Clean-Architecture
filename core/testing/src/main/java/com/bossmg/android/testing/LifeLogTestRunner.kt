package com.bossmg.android.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import kotlin.jvm.java
import dagger.hilt.android.testing.HiltTestApplication

class LifeLogTestRunner: AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application? {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}