package com.mytest.composetest.performance

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mytest.composetest.ui.theme.ComposeTestTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDateTime

@Composable
fun FoodList(
    modifier: Modifier = Modifier,
    foods: ImmutableList<FoodInfo>
) {

    var selectAll by remember {
        mutableStateOf(false)
    }

    Column {
        Switch(checked = selectAll, onCheckedChange = { selectAll = it })
        Spacer(modifier = Modifier.padding(10.dp))
        LazyColumn {
            items(foods) {
                Text(it.name)
            }
        }
    }
}

@Composable
fun FoodCard(
    modifier: Modifier = Modifier,
    food: FoodInfo
) {

    var selectAll by remember {
        mutableStateOf(false)
    }

    Column {
        Switch(checked = selectAll, onCheckedChange = { selectAll = it })
        Spacer(modifier = Modifier.padding(10.dp))
        FoodItem(food = food)
    }
}

@Composable
fun FoodItem(food: FoodInfo) {
    Text(food.name)
}

@Immutable // or @Stable
data class FoodInfo(val name: String, val timestamp: LocalDateTime)

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Performance4DefaultPreview() {
    ComposeTestTheme {
        FoodList(foods = listOf(FoodInfo("Meat ball", LocalDateTime.now())).toImmutableList())
    }
}