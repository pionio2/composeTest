package com.mytest.composetest.billing.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import coil.annotation.ExperimentalCoilApi
import com.mytest.composetest.billing.SkuInfo

import com.mytest.composetest.ui.theme.ComposeTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class BillingTestActivity : AppCompatActivity() {

    private val viewModel: BillingTestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                val skuInfoList by viewModel.skuInfoList.collectAsState()
                BillingTestScreen(skuInfoList)
            }
        }
    }
}

@Composable
fun BillingTestScreen(skuInfoList: List<SkuInfo>) {
    if (skuInfoList.isEmpty()) {
        //do something...
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(items = skuInfoList) { skuInfo ->
                ProductCard(skuInfo)
                Spacer(modifier = Modifier.padding(10.dp))
            }
        }
    }
}

@Composable
fun ProductCard(skuInfo: SkuInfo) {
    Surface(color = Color.LightGray, shape = RoundedCornerShape(5.dp)) {
        skuInfo.skuDetails?.let {
            Column {
                Text(it.title)
                Text(it.price)
                Text(it.description)
            }
        }
    }
}


@ExperimentalCoilApi
@InternalCoroutinesApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        BillingTestScreen(listOf())
    }
}