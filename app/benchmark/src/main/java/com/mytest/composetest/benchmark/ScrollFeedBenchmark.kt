package com.mytest.composetest.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ScrollFeedBenchmark {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun scrollFeed() = rule.measureRepeated(
        packageName = "com.mytest.composetest",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        setupBlock = {
            pressHome()
            startActivityAndWait()

            device.wait(Until.hasObject(By.text("What are you interested in? ")), 30_000)

            val authors = device.findObject(By.res("forYou:authors"))
            repeat(3) {
                authors.children[it].click()
            }
        }

    ) {
        val feedList = device.findObject(By.res("forYou:authors"))
        feedList.setGestureMargin(device.displayWidth / 5)
        feedList.fling(Direction.DOWN)
    }
}