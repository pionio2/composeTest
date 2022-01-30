package com.mytest.composetest.billing.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import coil.annotation.ExperimentalCoilApi
import com.mytest.composetest.restful.Picture
import com.mytest.composetest.restful.RestFulTestScreen
import com.mytest.composetest.ui.theme.ComposeTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class BillingTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                BillingTestScreen()
            }
        }
    }
}

@Composable
fun BillingTestScreen() {

}

@ExperimentalCoilApi
@InternalCoroutinesApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        BillingTestScreen()
    }
}