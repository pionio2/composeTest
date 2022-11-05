package com.mytest.composetest.friend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.mytest.composetest.friend.data.FriendModel
import com.mytest.composetest.ui.theme.ComposeTestTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.mytest.composetest.util.LogDebug

private const val TAG = "FriendMainView"

@Composable
fun FriendsListMainView(modifier: Modifier = Modifier, friendPagingItems: LazyPagingItems<FriendModel>) {
    val isLoading by remember(friendPagingItems.loadState.source) {
        derivedStateOf {
            when {
                friendPagingItems.loadState.source.refresh == LoadState.Loading ||
                friendPagingItems.loadState.source.prepend == LoadState.Loading ||
                friendPagingItems.loadState.source.append == LoadState.Loading -> {
                    LogDebug(TAG) {"list state is loading:${friendPagingItems.loadState.source}"}
                    true
                }
                else -> false
            }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        // 목록 표시
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)

        ) {
            items(items = friendPagingItems,
                key = { friend -> friend.id }) { item ->
                item?.let { FriendCard(friend = it) }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun FriendCard(modifier: Modifier = Modifier, friend: FriendModel) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                "${friend.name}(id: ${friend.id})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(friend.phoneNumber, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        FriendCard(friend = FriendModel(1, "홍길동", "010-9999-9999"))
    }
}