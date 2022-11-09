package com.mytest.composetest.friend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import com.mytest.composetest.friend.data.FriendModel
import com.mytest.composetest.friend.ui.IndexedScroll
import com.mytest.composetest.friend.ui.TextLabel
import com.mytest.composetest.ui.theme.ComposeTestTheme
import com.mytest.composetest.util.LogDebug
import kotlinx.coroutines.launch

private const val TAG = "FriendMainView"

@Composable
fun FriendsListMainView(
    modifier: Modifier = Modifier,
    friendStatus: FriendLoadingStatus,
    onClickAction: (FriendClicks) -> Unit
) {

    Box(modifier = modifier.fillMaxSize()) {

        when (friendStatus) {
            is FriendLoadFailed -> {
                // 에러메시지 처리
            }
            FriendLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is FriendLoadSuccess -> FriendsList(friendsList = friendStatus.friends)
        }
    }
}

@Composable
fun FriendsPagingListMainView(
    modifier: Modifier = Modifier,
    friendPagingItems: LazyPagingItems<FriendModel>,
    onClickAction: (FriendClicks) -> Unit
) {
    LogDebug(TAG) { "count: ${friendPagingItems.itemCount}" }

    // lading state 추적.
    val isLoading by remember(friendPagingItems.loadState.source) {
        derivedStateOf {
            when {
                friendPagingItems.loadState.source.refresh == LoadState.Loading ||
                        friendPagingItems.loadState.source.prepend == LoadState.Loading ||
                        friendPagingItems.loadState.source.append == LoadState.Loading -> {
                    LogDebug(TAG) { "list state is loading:${friendPagingItems.loadState.source}" }
                    true
                }
                else -> false
            }
        }
    }

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        Column {
            Button(onClick = {
                coroutineScope.launch { scrollState.scrollToItem(500) }
            }) {
                Text("Jump to 500 position")
            }
            // 목록 표시
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state = scrollState

            ) {
                itemsIndexed(
                    key = { _, friend -> friend.id },
                    items = friendPagingItems
                ) { index, item ->
                    if (item == null) {
                        LoadItemCard()
                    } else {
                        FriendCard(friend = item, index = index)
                    }
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun FriendsList(modifier: Modifier = Modifier, friendsList: List<FriendModel>) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        Button(onClick = {
            coroutineScope.launch { scrollState.scrollToItem(500) }
        }) {
            Text("Jump to 500 position")
        }
        Box {
            // 목록 표시
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state = scrollState

            ) {
                itemsIndexed(
                    key = { _, friend -> friend.id },
                    items = friendsList
                ) { index, item ->
                    FriendCard(friend = item, index = index)
                }
            }
            IndexedScroll(
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, end = 5.dp),
                labelList = listOf(
                    TextLabel("ㄱ"),
                    TextLabel("ㄴ"),
                    TextLabel("ㄷ"),
                    TextLabel("ㄹ"),
                    TextLabel("ㅁ"),
                    TextLabel("ㅂ"),
                    TextLabel("ㅅ"),
                    TextLabel("ㅇ"),
                    TextLabel("ㅈ"),
                    TextLabel("ㅊ"),
                    TextLabel("ㅋ"),
                    TextLabel("ㅌ"),
                    TextLabel("ㅍ"),
                    TextLabel("ㅎ"),
                    TextLabel("#"),
                    TextLabel("ㄱ"),
                    TextLabel("ㄴ"),
                    TextLabel("ㄷ"),
                    TextLabel("ㄹ"),
//                    TextLabel("ㅁ"),
//                    TextLabel("ㅂ"),
//                    TextLabel("ㅅ"),
//                    TextLabel("ㅇ"),
//                    TextLabel("ㅈ"),
//                    TextLabel("ㅊ"),
//                    TextLabel("ㅋ"),
//                    TextLabel("ㅌ"),
//                    TextLabel("ㅍ"),
//                    TextLabel("ㅎ"),
//                    TextLabel("*"),
            ), onHovered = {})
        }
    }
}

@Composable
fun FriendCard(modifier: Modifier = Modifier, index: Int, friend: FriendModel) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Text(text = index.toString(), modifier = Modifier.padding(4.dp))
            Column() {
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
}

@Composable
fun LoadItemCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.Gray
    ) {
        Text("Loading", modifier = Modifier.padding(4.dp))

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        Column {
            FriendCard(
                index = 1,
                friend = FriendModel(1, "홍길동", "010-9999-9999", "ㅎ", System.currentTimeMillis()),
            )
            LoadItemCard(modifier = Modifier.padding(top = 2.dp))
        }
    }
}