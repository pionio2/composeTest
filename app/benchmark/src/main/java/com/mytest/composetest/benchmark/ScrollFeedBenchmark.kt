package com.mytest.composetest.benchmark

import android.graphics.Point
import android.util.Log
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ScrollFriendListBenchmark {
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

//            device.wait(Until.hasObject(By.text("What are you interested in? ")), 30_000)
//
//            val authors = device.findObject(By.res("forYou:authors"))
//            repeat(3) {
//                authors.children[it].click()
//            }
        }

    ) {
        val friendList = device.findObject(By.res("friend_list"))
        println("friendList:$friendList")
        val searchCondition = Until.hasObject(By.res("friend_item"))
        println("searchCondition:$searchCondition")
        // Wait until a snack collection item within the list is rendered
        friendList.wait(searchCondition, 5_000L)

        // Set gesture margin to avoid triggering gesture navigation
        friendList.setGestureMargin(device.displayWidth / 5)

        // Scroll down the list
        friendList.fling(Direction.DOWN)

        // Wait for the scroll to finish
        device.waitForIdle()
    }

    @Test
    fun dragFeed() = rule.measureRepeated(
        packageName = "com.mytest.composetest",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        setupBlock = {
            pressHome()
            startActivityAndWait()

//            device.wait(Until.hasObject(By.text("What are you interested in? ")), 30_000)
//
//            val authors = device.findObject(By.res("forYou:authors"))
//            repeat(3) {
//                authors.children[it].click()
//            }
        }

    ) {
        val friendList = device.findObject(By.res("friend_list"))
        println("friendList:$friendList")
        val searchCondition = Until.hasObject(By.res("friend_item"))
        println("searchCondition:$searchCondition")
        // Wait until a snack collection item within the list is rendered
        friendList.wait(searchCondition, 5_000L)

        // Set gesture margin to avoid triggering gesture navigation
        friendList.setGestureMargin(device.displayWidth / 5)

        // Scroll down the list
        repeat(3) {
            friendList.drag(Point(0, friendList.visibleCenter.y / 3))
        }

        // Wait for the scroll to finish
        device.waitForIdle()
    }
}