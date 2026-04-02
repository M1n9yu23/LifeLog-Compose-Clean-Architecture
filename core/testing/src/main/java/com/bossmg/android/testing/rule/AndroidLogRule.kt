package com.bossmg.android.testing.rule

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.rules.ExternalResource

class AndroidLogRule : ExternalResource() {
    override fun before() {
        mockkStatic(Log::class)

        every { Log.v(any(), any()) } returns 0
        every { Log.v(any(), any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.i(any(), any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.w(any(), any<Throwable>()) } returns 0
        every { Log.w(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.wtf(any(), any<String>()) } returns 0
        every { Log.wtf(any(), any<Throwable>()) } returns 0
    }

    override fun after() {
        unmockkStatic(Log::class)
    }
}
