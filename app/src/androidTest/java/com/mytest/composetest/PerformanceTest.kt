package com.mytest.composetest

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.mytest.composetest.friend.FriendLoadSuccess
import com.mytest.composetest.friend.FriendsListMainView
import org.junit.Rule
import org.junit.Test

class PerformanceTest {
    @get:Rule
    val rule = createComposeRule()

    // activity에 접근하고자 할때 e.g. string res에 접근이 필요할때
    val rule2 = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun enterContacts_showContactList() {

        rule.setContent { FriendsListMainView(friendStatus = FriendLoadSuccess(listOf())) {}  }

//        val errorStr = rule2.activity.getString(R.string.app_name)
    }
}