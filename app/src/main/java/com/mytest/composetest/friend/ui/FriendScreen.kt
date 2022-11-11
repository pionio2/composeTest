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
import androidx.paging.compose.itemsIndexed
import com.mytest.composetest.friend.data.FriendUiModel
import com.mytest.composetest.friend.ui.IndexedScroll
import com.mytest.composetest.friend.ui.TextLabel
import com.mytest.composetest.ui.theme.ComposeTestTheme
import com.mytest.composetest.util.LogDebug
import com.mytest.composetest.util.LogError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "FriendMainView"

/**
 * 친구목록 Main View
 */
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

/**
 * 친구목록 View - Paging 사용시
 */
@Composable
fun FriendsPagingListMainView(
    modifier: Modifier = Modifier,
    friendPagingItems: LazyPagingItems<FriendUiModel>,
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
                    key = { _, friend -> friend.dbId },
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
fun FriendsList(
    modifier: Modifier = Modifier,
    friendsList: List<FriendUiModel>,
    favoriteList: List<FriendUiModel> = listOf(),
    searchIndexEnable: Boolean = true,
    FavoriteIndexEnable: Boolean = true
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // scroll index에 들어갈 라벨을 만든다.
    val labelList by getIndexScrollLabels(friendsList, searchIndexEnable, FavoriteIndexEnable)

    // scroll index라벨에 따라 이동 할 list의 position을 구한다.
    val labelScrollMap by calculatePositionForLabel(friendsList, favoriteList, labelList)

    Column(modifier = modifier.fillMaxSize()) {
        Box {
            // 목록 표시
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state = scrollState

            ) {
                itemsIndexed(
                    key = { _, friend -> friend.dbId },
                    items = friendsList
                ) { index, item ->
                    FriendCard(friend = item, index = index)
                }
            }
            // Indexed scroll 표시
            IndexedScroll(
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, end = 5.dp),
                labelList = IndexedScroll.getIndexLabel(labelList),
                scrollState = scrollState,
                ) {
                coroutineScope.launch { scrollState.scrollToItem(labelScrollMap.getOrDefault(it, 0)) }
            }
        }
    }
}

// indexed scroll에 사용할 label을 구한다.
// e.g. 검색 + 즐겨찾기 + 한글 + 영어
// e.g. 즐겨찾기 + 영어 + 한글
@Composable
fun getIndexScrollLabels(
    friendsList: List<FriendUiModel>,
    searchIndexEnable: Boolean = true,
    FavoriteIndexEnable: Boolean = true
): State<List<IndexedScroll.ScrollIndexType>> {

    // 초기값
    val initValue = mutableListOf<IndexedScroll.ScrollIndexType>()
    if (searchIndexEnable) initValue.add(IndexedScroll.ScrollIndexType.SEARCH)
    if (FavoriteIndexEnable) initValue.add(IndexedScroll.ScrollIndexType.FAVORITE)

    return produceState(initialValue = initValue) {
        val list = mutableListOf<IndexedScroll.ScrollIndexType>()
        if (searchIndexEnable) list.add(IndexedScroll.ScrollIndexType.SEARCH)
        if (FavoriteIndexEnable) list.add(IndexedScroll.ScrollIndexType.FAVORITE)

        // 친구리스트 첫번째 항목의 label을 꺼내서 영어인지 판단한다.
        val isStartFromEnglish = if (friendsList.isNotEmpty() && friendsList[0].nameLabel.isNotBlank()) {
            val labelChar = friendsList[0].nameLabel.toCharArray()[0]
            labelChar in 'a'..'z' || labelChar in 'A'..'Z'
        } else {
            false
        }

        // 영어 -> 한글 / 한글 -> 영어 index를 사용할지 결정한다.
        if (isStartFromEnglish) {
            list.add(IndexedScroll.ScrollIndexType.ENGLISH_KOREAN)
        } else {
            list.add(IndexedScroll.ScrollIndexType.KOREAN_ENGLISH)
        }

        value = list
    }
}

/**
 * 라벨에 맞춰 scroll할 position 위치를 계산한다.
 * return값에는 map<label position, 스크롤 position>이 담긴다.
 *
 * IMPL_NOTE: favorite 같은 친구 위에 존재하는 리스트가 존재하면 친구 리스트의 position은 item list의 position에 상단에 위치한 row 개수만큼 더해야 한다.
 * 추후 이런 상단에 위치히는 항목들이 늘어나면 여기에 추가되어야 한다.
 */
@Composable
fun calculatePositionForLabel(
    friendsList: List<FriendUiModel>,
    favoriteList: List<FriendUiModel>,
    labelList: List<IndexedScroll.ScrollIndexType>
): State<Map<Int, Int>> {
    LogDebug(TAG) {"calculatePositionForLabel() -  start position calculating"}
    // 계산되기전 초기값은 empty map이다. 이때 scroll하면 null이 떨어지므로 0으로 이동해야함.
    val initMap = mutableMapOf<Int, Int>()

    return produceState(initialValue = initMap, friendsList, labelList, favoriteList.size) {
        val calculatedMap = withContext(Dispatchers.Default) {
            val resultMap = mutableMapOf<Int, Int>()

            // character 스크롤 보다 favorite이 상위에 존재하므로 해당 개수만큼 더해서 scroll 해야 한다.
            val characterOffset = favoriteList.size

            // 전체 indexLabel을 펼쳐서 Pair(IndexedScroll.ScrollIndexType, IndexLabel)의 list 형태로 만든다.
            val indexTypeAndLabelPairList = labelList.flatMap { scrollIndexType -> scrollIndexType.labelList.map { Pair(scrollIndexType, it) } }

            coroutineScope {
                //1. index label 기준으로 아이템 list에서 position을 찾는다.
                indexTypeAndLabelPairList.forEachIndexed { index, scrollIndexTypeAndValue ->
                    when (scrollIndexTypeAndValue.first) {
                        // 검색은 최상단이므로 맨 위로 올린다.
                        IndexedScroll.ScrollIndexType.SEARCH -> resultMap[index] = 0

                        // 즐겨찾기는 시작
                        // INPLE_NOTE character이동 position을 구하기 전에 즐겨찾기 같은 예외 항목이 더 생긴다면 여기에 추가하고 characterOffset에 해당 개수만큼 더해져야 한다.
                        IndexedScroll.ScrollIndexType.FAVORITE -> resultMap[index] = 0

                        // 영어 / 한글에 대한 scroll할 position을 구한다. (각 라벨별로 병렬처리한다. launch별 map의 key index가 모두 다르므로 동기화 이슈 없음)
                        IndexedScroll.ScrollIndexType.KOREAN_ENGLISH,
                        IndexedScroll.ScrollIndexType.ENGLISH_KOREAN -> launch {
                            if (index == indexTypeAndLabelPairList.lastIndex) {
                                // 마지막 인덱스는 special character 이므로 "#" 다른 계산이 다 끝난후 계산한다.
                                resultMap[index] = -1
                            } else {
                                val indexLabel = scrollIndexTypeAndValue.second
                                if (indexLabel is TextLabel) {
                                    val label = indexLabel.label
                                    val firstIndexOfLabel = friendsList.indexOfFirst { it.nameLabel == label }
                                    if (firstIndexOfLabel != -1) {
                                        // 해당 label에 해당하는 item이 존재하면 position에 앞서 추가되어 있는 항목(e.g.즐겨찾기)의 offset을 추가한다.
                                        resultMap[index] = firstIndexOfLabel + characterOffset
                                    } else {
                                        // 해당 label에 해당하는 item이 list에 존재하지 않는경우 e.g. "ㄱ" 라벨에 해당하는 item이 list에 없음.
                                        resultMap[index] = -1
                                    }
                                } else {
                                    // TextLabel이 아닌 경우 진입 -> 단 KOREAN_ENGLISH의 경우 TextLabel만 사용하므로 (실제로 여기 들어올수 없다.)
                                    resultMap[index] = -1
                                }
                            }
                        }
                    }
                }
            }

            LogDebug(TAG) { "calculatePositionForLabel() - exist label map: $resultMap" }

            if (resultMap.isEmpty()) {
                LogError(TAG) { "resultMap is empty" }
            } else {
                //마지막 label (special character 인 '#')의 시작시점(scroll position)을 찾는다. -> 직전 라벨중 값이 존재하는 라벨 position + 1
                var specialCharacterPosition = 0
                for (i in (resultMap.size - 1) downTo 0) {
                    val position = resultMap[i]
                    if (position != null && position != -1) {
                        specialCharacterPosition = position
                        break
                    }
                }
                resultMap[resultMap.size - 1] = specialCharacterPosition
                LogDebug(TAG) { "calculatePositionForLabel() - last label position: $specialCharacterPosition" }

                // item이 존재하지 않는 label (값이 -1)인 항목들의 position을 정한다.
                // e.g. "ㄱ"이 존재하는 아이템이 없다면 "ㄱ" 스크롤시 다음 값인 "ㄴ"로 이동해야 한다. 이때 "ㄴ"도 없다면 그 다음에 존재하는 위치로 이동한다.
                var nextPosition = 0
                val foundIndexMap = mutableMapOf<Int, Int>()
                for (i in (resultMap.size - 1) downTo 0) {
                    val position = resultMap[i]
                    if (position != null && position != -1) {
                        nextPosition = position
                    } else {
                        foundIndexMap[i] = nextPosition
                    }
                }

                LogDebug(TAG) { "calculatePositionForLabel() - miss label position: $foundIndexMap" }

                for ((key, value) in foundIndexMap) {
                    resultMap[key] = value
                }
            }

            LogDebug(TAG) { "calculatePositionForLabel() - $resultMap" }
            resultMap
        }
        value = calculatedMap
    }
}

@Composable
fun FriendCard(modifier: Modifier = Modifier, index: Int, friend: FriendUiModel) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Text(text = index.toString(), modifier = Modifier.padding(4.dp))
            Column() {
                Text(
                    "${friend.name}(id: ${friend.dbId})",
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
                friend = FriendUiModel(1, "홍길동", "010-9999-9999", "ㅎ", System.currentTimeMillis()),
            )
            LoadItemCard(modifier = Modifier.padding(top = 2.dp))
        }
    }
}