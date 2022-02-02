package com.mytest.composetest.billing.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import coil.annotation.ExperimentalCoilApi
import com.android.billingclient.api.SkuDetails
import com.mytest.composetest.billing.ProductType
import com.mytest.composetest.billing.SkuInfo

import com.mytest.composetest.ui.theme.ComposeTestTheme
import com.mytest.composetest.util.LogError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillingTestActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BillingTestActivity"
    }

    private val viewModel: BillingTestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = this
        lifecycle.addObserver(viewModel)

        setContent {
            ComposeTestTheme {

                // 상품 목록
                val skuInfoList by viewModel.skuInfoList.collectAsState()
                // snackBar 메시지
                val snackBarMsg by viewModel.snackbarMessage.collectAsState(initial = SnackbarMessage())

                val scope = rememberCoroutineScope()
                val scaffoldState = rememberScaffoldState()

                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("상품 구매 리스트") })
                    },
                    scaffoldState = scaffoldState
                ) { innerPadding ->
                    BillingTestScreen(
                        modifier = Modifier.padding(innerPadding),
                        skuInfoList = skuInfoList
                    ) {
                        scope.launch {
                            viewModel.onPurchaseClicked(activity, it)
                        }
                    }

                    // 스낵바로 메시지를 띄운다.
                    if (snackBarMsg.message.isNotEmpty()) {
                        LaunchedEffect(snackBarMsg) {
                            scaffoldState.snackbarHostState.showSnackbar(snackBarMsg.message)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BillingTestScreen(
    modifier: Modifier = Modifier,
    skuInfoList: List<SkuInfo>,
    onClick: (SkuInfo) -> Unit
) {
    if (skuInfoList.isEmpty()) {
        //do something...
    } else {
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(items = skuInfoList) { skuInfo ->
                ProductCard(skuInfo, onClick)
                Divider(modifier = Modifier.padding(2.dp))
            }
        }
    }
}

@Composable
fun ProductCard(skuInfo: SkuInfo, onClick: (SkuInfo) -> Unit) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(skuInfo) }
        .padding(5.dp)) {
        skuInfo.skuDetails?.let {
            Column(modifier = Modifier.padding(5.dp)) {
                Text(it.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row {
                    Text("가격: ", fontSize = 12.sp)
                    Text(it.price, fontSize = 12.sp, color = Color.Red)
                }
                Text(it.description, fontSize = 12.sp)
                Text(
                    text = getPurchaseStatusText(skuInfo.skuState),
                    modifier = Modifier.align(Alignment.End),
                    color = getPurchaseStatusColor(skuInfo.skuState)
                )
            }
        }
    }
}

private fun getPurchaseStatusText(status: SkuInfo.SkuState) = when (status) {
    SkuInfo.SkuState.SKU_STATE_PURCHASED,
    SkuInfo.SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED -> "구매완료"
    SkuInfo.SkuState.SKU_STATE_PENDING -> "구매 대기중"
    SkuInfo.SkuState.SKU_STATE_UNPURCHASED -> "구매 가능"
}

private fun getPurchaseStatusColor(status: SkuInfo.SkuState) = when (status) {
    SkuInfo.SkuState.SKU_STATE_PURCHASED,
    SkuInfo.SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED -> Color.Black
    SkuInfo.SkuState.SKU_STATE_PENDING -> Color.Red
    SkuInfo.SkuState.SKU_STATE_UNPURCHASED -> Color.Blue
}


@ExperimentalCoilApi
@InternalCoroutinesApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        BillingTestScreen(
            skuInfoList = listOf(
                SkuInfo(
                    SkuInfo.SkuState.SKU_STATE_PURCHASED, ProductType.SUBSCRIPTION,"basic_1_month",
                    SkuDetails(
                        """{"productId":"basic_1_month","type":"subs","title":"Basic 한달 사용권 (com.mytest.composetest (unreviewed))","name":"Basic 한달 사용권","price":"₩9,900",
                        "price_amount_micros":9900000000,"price_currency_code":"KRW","description":"Basic 등급 한달 사용권","subscriptionPeriod":"P1M","skuDetailsToken":"AEuhp4K5UMrJXD_qGl5qv2mo5uap1F1MWzIVbfR4_zNdJRWRH1PF44nsmK5uTBM0ZVfX"}
                    """
                    )
                ),
                SkuInfo(
                    SkuInfo.SkuState.SKU_STATE_UNPURCHASED, ProductType.CONSUMABLE,"theme_5_charge",
                    SkuDetails(
                        """{"productId":"theme_5_charge","type":"inapp","title":"테마 5개 구매 충전 (com.mytest.composetest (unreviewed))","name":"테마 5개 구매 충전","price":"₩4,000",
                            "price_amount_micros":4000000000,"price_currency_code":"KRW","description":"테마 5개 구매 충전","skuDetailsToken":"AEuhp4IZaoFENa_AO4nfQtmugychdxFXu0M9OW0PMO9zrQUo_CZfiByXaUeiIdC2SVvD"}"""
                    )
                )
            )
        ) {}
    }
}