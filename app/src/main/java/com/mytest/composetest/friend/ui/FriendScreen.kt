package com.mytest.composetest.friend

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.mytest.composetest.friend.data.FriendListUiType
import com.mytest.composetest.friend.data.FriendUiHeader
import com.mytest.composetest.friend.data.FriendUiItem
import com.mytest.composetest.friend.data.FriendUiModel
import com.mytest.composetest.friend.ui.IndexedScroll
import com.mytest.composetest.friend.ui.TextLabel
import com.mytest.composetest.ui.theme.ComposeTestTheme
import com.mytest.composetest.util.LogDebug
import com.mytest.composetest.util.LogError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*
import java.util.regex.Pattern

private const val TAG = "FriendMainView"
private const val POSITION_NOT_FOUND = -1
private const val POSITION_START = 0

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
    friendPagingItems: LazyPagingItems<FriendUiItem>,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FriendsList(
    modifier: Modifier = Modifier,
    friendsList: ImmutableList<FriendUiModel>,
    searchIndexEnable: Boolean = true,
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // scroll index에 들어갈 라벨을 만든다.
    val labelList by getIndexScrollLabels(friendsList, searchIndexEnable)

    // scroll index라벨에 따라 이동 할 list의 position을 구한다.
    val labelScrollMap by calculatePositionForLabel(friendsList, labelList)

    Column(modifier = modifier.fillMaxSize()) {
        Box {
            // 목록 표시
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("friend_list"),
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state = scrollState
            ) {
                itemsIndexed(
                    key = { _, friend -> friend.uiId },
                    items = friendsList,
                    contentType = { _, item -> item.uiType } // LazyColumn 속도 향상을 위한 type 명시
                ) { index, item ->
                    when (item) {
                        is FriendUiHeader -> FriendHeader(
                            modifier = Modifier
                                .testTag("friend_item")
                                .animateItemPlacement(),
                            friend = item
                        )
                        is FriendUiItem -> FriendCard(
                            modifier = Modifier
                                .testTag("friend_item")
                                .animateItemPlacement(),
                            friend = item,
                            index = index
                        )
                    }
                }
            }
            // Indexed scroll 표시
            IndexedScroll(
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, end = 5.dp),
                labelList = IndexedScroll.getIndexLabel(labelList),
                isScrollState = scrollState.isScrollInProgress,
            ) {
                coroutineScope.launch { scrollState.scrollToItem(labelScrollMap.getOrDefault(it, POSITION_START)) }
            }
        }
    }
}

// indexed scroll에 사용할 label을 구한다.
// e.g. 검색 + 즐겨찾기 + 한글 + 영어
// e.g. 즐겨찾기 + 영어 + 한글
@Composable
fun getIndexScrollLabels(
    friendsList: ImmutableList<FriendUiModel>,
    searchIndexEnable: Boolean = true
): State<ImmutableList<IndexedScroll.ScrollIndexType>> {

    // 초기값
    val initValue = mutableListOf<IndexedScroll.ScrollIndexType>()
    if (searchIndexEnable) initValue.add(IndexedScroll.ScrollIndexType.SEARCH)

    val context = LocalContext.current

    return produceState(initialValue = initValue.toImmutableList()) {
        val list = mutableListOf<IndexedScroll.ScrollIndexType>()
        // 검색 아이콘 추가
        if (searchIndexEnable) list.add(IndexedScroll.ScrollIndexType.SEARCH)

        // 에이닷 아이콘 추가
        val adotIndexEnable = withContext(Dispatchers.Default) {
            friendsList.any { it.uiType is FriendListUiType.AdotItem }
        }
        if (adotIndexEnable) list.add(IndexedScroll.ScrollIndexType.ADOT)

        // 즐겨찾기 아이콘 추가
        val favoriteIndexEnable = withContext(Dispatchers.Default) {
            friendsList.any { it.uiType is FriendListUiType.FavoriteItem }
        }
        if (favoriteIndexEnable) list.add(IndexedScroll.ScrollIndexType.FAVORITE)

        // 영어 -> 한글 / 한글 -> 영어 index를 사용할지 결정한다.
        val locales = context.resources.configuration.locales
        LogDebug(TAG) { "getIndexScrollLabels() - locales: $locales" }
        if (locales.isEmpty || (locales[0] != Locale.KOREAN && locales[0] != Locale.KOREA)) {
            LogDebug(TAG) { "getIndexScrollLabels() - english scroll list selected" }
            list.add(IndexedScroll.ScrollIndexType.ENGLISH_KOREAN)
        } else {
            LogDebug(TAG) { "getIndexScrollLabels() - korea scroll list selected" }
            list.add(IndexedScroll.ScrollIndexType.KOREAN_ENGLISH)
        }

        value = list.toImmutableList()
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
    friendsList: ImmutableList<FriendUiModel>,
    labelList: ImmutableList<IndexedScroll.ScrollIndexType>
): State<Map<Int, Int>> {
    LogDebug(TAG) { "calculatePositionForLabel() -  start position calculating" }
    // 계산되기전 초기값은 empty map이다. 이때 scroll하면 null이 떨어지므로 0으로 이동해야함.
    val initMap = mutableMapOf<Int, Int>()

    return produceState(initialValue = initMap, friendsList, labelList) {
        val calculatedMap = withContext(Dispatchers.Default) {
            val resultMap = mutableMapOf<Int, Int>()

            if (friendsList.isEmpty() || labelList.isEmpty()) {
                return@withContext resultMap
            }

            // 전체 indexLabel을 펼쳐서 Pair(IndexedScroll.ScrollIndexType, IndexLabel)의 list 형태로 만든다.
            val indexTypeAndLabelPairList = labelList.flatMap { scrollIndexType -> scrollIndexType.labelList.map { Pair(scrollIndexType, it) } }

            coroutineScope {
                //1. index label 기준으로 아이템 list에서 position을 찾는다.
                indexTypeAndLabelPairList.forEachIndexed { index, scrollIndexTypeAndValue ->
                    when (scrollIndexTypeAndValue.first) {
                        // 검색은 최상단이므로 맨 위로 올린다.
                        IndexedScroll.ScrollIndexType.SEARCH -> resultMap[index] = POSITION_START

                        // adot 시작점
                        IndexedScroll.ScrollIndexType.ADOT -> launch {
                            val position = friendsList.indexOfFirst { it.uiType is FriendListUiType.AdotItem }
                            resultMap[index] = max(position - 1, POSITION_START)   //header로 이동하기 위해서 첫번째 즐겨찾기 아이템에서 1을 뺀다.
                        }

                        // 즐겨찾기는 시작
                        IndexedScroll.ScrollIndexType.FAVORITE -> launch {
                            val position = friendsList.indexOfFirst { it.uiType is FriendListUiType.FavoriteItem }
                            resultMap[index] = max(position - 1, POSITION_START)   //header로 이동하기 위해서 첫번째 즐겨찾기 아이템에서 1을 뺀다.
                        }

                        // 영어 / 한글에 대한 scroll할 position을 구한다.
                        // 각 라벨별로 병렬처리한다. (launch별 map의 key index가 모두 다르므로 resultMap에 대한 동기화 이슈 없음)
                        IndexedScroll.ScrollIndexType.KOREAN_ENGLISH,
                        IndexedScroll.ScrollIndexType.ENGLISH_KOREAN -> launch {
                            val indexLabel = scrollIndexTypeAndValue.second
                            if (indexLabel is TextLabel) {
                                val label = indexLabel.label
                                val firstPositionOfLabel = friendsList.indexOfFirst {
                                    it is FriendUiItem && it.uiType == FriendListUiType.FriendItem && it.nameLabel == label
                                }
                                resultMap[index] = firstPositionOfLabel //못찾았을 경우 -1이 이 들어간다..
                            } else {
                                // TextLabel이 아닌 경우 진입 -> 단 KOREAN_ENGLISH / ENGLISH_KOREAN 의 경우 TextLabel만 사용하므로 (실제로 여기 들어올수 없다.)
                                resultMap[index] = POSITION_START
                                LogError(TAG) { "calculatePositionForLabel() - error!!" }
                            }
                        }
                    }
                }
            }

            LogDebug(TAG) { "calculatePositionForLabel() - exist label map: $resultMap" }

            if (resultMap.isEmpty()) {
                LogError(TAG) { "resultMap is empty" }
            } else {
                // 마지막 index '#'의 위치를 구한다.
                var validLastPosition = 0
                for (i in (resultMap.size - 1) downTo 0) {
                    val position = resultMap[i]
                    if (position != null && position != POSITION_NOT_FOUND) {
                        validLastPosition = position
                        break
                    }
                }
                LogDebug(TAG) { "resultMap last position: $validLastPosition " }
                resultMap[resultMap.size - 1] = validLastPosition

                // item이 존재하지 않는 label (값이 -1)인 항목들의 position을 정한다.
                // e.g. "ㄱ"이 존재하는 아이템이 없다면 "ㄱ" 스크롤시 다음 값인 "ㄴ"로 이동해야 한다. 이때 "ㄴ"도 없다면 그 다음에 존재하는 위치로 이동한다.
                var nextPosition = POSITION_START
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

// item 친구 목록
@Composable
fun FriendCard(modifier: Modifier = Modifier, index: Int, friend: FriendUiItem) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Text(text = index.toString(), modifier = Modifier.padding(4.dp))
            Column() {
                Text(
                    "${friend.name}(uiId: ${friend.uiId})",
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

// item 헤더 view
@Composable
fun FriendHeader(modifier: Modifier = Modifier, friend: FriendUiHeader) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            friend.title, modifier = Modifier.padding(4.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Divider()
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
                friend = FriendUiItem(
                    1, "홍길동", "010-9999-9999", "ㅎ",
                    createDate = System.currentTimeMillis(), uiId = 1, uiType = FriendListUiType.FriendItem
                ),
            )
            LoadItemCard(modifier = Modifier.padding(top = 2.dp))
            FriendHeader(friend = FriendUiHeader("헤더 샘플", uiId = 10101010))
        }
    }
}