package com.mytest.composetest.performance


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.mytest.composetest.ui.theme.ComposeTestTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.*
import coil.compose.rememberAsyncImagePainter
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDateTime

@AndroidEntryPoint
class PerformanceActivity : ComponentActivity() {

    val viewModel: PerformanceViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {

                val contactList by viewModel.contactList.collectAsState()

                Scaffold(modifier = Modifier.semantics {
                    // uiAutomator 사용을 위한 추가.
                    testTagsAsResourceId = true
                }) { scaffoldPadding ->
//                    ContactList(
//                        modifier = Modifier.padding(scaffoldPadding),
//                        contacts = contactList,
//                        headerText = "전체목록"
//                    )

//                    FoodList(
//                        Modifier.padding(scaffoldPadding),
//                        listOf(
//                            FoodInfo("Meat ball", LocalDateTime.now()),
//                            FoodInfo("Pizza",LocalDateTime.now()),
//                            FoodInfo("Chicken",LocalDateTime.now())
//                        ).toImmutableList()
//                    )

//                    FoodCard(Modifier.padding(scaffoldPadding), FoodInfo("Meat ball"))

//                    DeriveStateOfSample2(Modifier.padding(scaffoldPadding))
//                    DeriveStateOfSample(Modifier.padding(scaffoldPadding))

//                    PerformanceTestScreen(Modifier.padding(scaffoldPadding))


                      // 버튼 + 카운트 예제
                    val screenState by viewModel.screenState.collectAsState()
                    val checkedChanged = remember<(Boolean) -> Unit> {
                        { viewModel.setSwitchValue(it) }
                    }
                    val increaseCount =  remember<() -> Unit> {
                        { viewModel.increaseCount() }
                    }

                    PerformanceTestScreen3(
                        Modifier.padding(scaffoldPadding),
                        screenState,
//                        { isChecked -> viewModel.setSwitchValue(isChecked) },
//                        { viewModel.increaseCount() }
                        checkedChanged,
                        increaseCount
                    )
                }
            }
        }
    }
}