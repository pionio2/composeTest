package com.mytest.composetest.friend

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytest.composetest.friend.data.*
import com.mytest.composetest.friend.db.FriendEntity
import com.mytest.composetest.friend.db.FriendSortOrder
import com.mytest.composetest.friend.db.FriendView
import com.mytest.composetest.friend.db.FriendsDatabase
import com.mytest.composetest.friend.ui.IndexedScroll
import com.mytest.composetest.util.LogDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FriendsListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val friendRepository: FriendsListRepository,
    private val friendDb: FriendsDatabase
) : ViewModel() {

    companion object {
        private const val TAG = "FriendsListViewModel"
        private const val FRIENDS_FLOW_TIMEOUT = 5000L
    }

    val friendsListFlow: StateFlow<FriendLoadingStatus>

    //paging 사용시 주석 제거
//    val friendsListFlow: Flow<PagingData<FriendModel>>

    init {
//        friendsListFlow = friendRepository.getFriendsPagingDataFlow()
//            .toFriendModel()
//            .cachedIn(viewModelScope)


        //TODO 언어 변경후 recent app으로 재진입하면 refresh가 되지 않는다. 추후 고칠것.
        //친구 목록 order는 (한글/영어/특수문자)는 언어 설정을 따른다.
        val friendsSortOrder = FriendSortOrder(FriendSortOrder.OrderType.DEVICE_LANGUAGE_SETTING)
        friendsListFlow = friendRepository.getFriendsFlow(friendsSortOrder)
            .map { friendsList ->
                LogDebug(TAG) { "Friends list loading start ${Thread.currentThread().name}" }
                val friendsUiList = makeTotalList(friendsList)
                LogDebug(TAG) { "Friends list loading complete ${Thread.currentThread().name}" }
                FriendLoadSuccess(friendsUiList) as FriendLoadingStatus
            }
            .flowOn(Dispatchers.IO)
            .catch { cause -> emit(FriendLoadFailed(cause)) }
            .conflate()
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = FRIENDS_FLOW_TIMEOUT),
                initialValue = FriendLoading
            )
    }

    // 친구목록에서 adot / 즐겨찾기 친구를 분류해서 전체목록 상단에 추가한다.
    private suspend fun makeTotalList(friendsList: List<FriendView>) = coroutineScope {
        // 에이닷 친구 분리
        val adots = async {
            friendsList.filter { it.friend.isAdotUser == FriendEntity.Columns.YES }
                .map { it.asDomain(FriendListUiType.AdotItem()) }
        }
        // 즐겨찾기 분리
        val favorites = async {
            friendsList.filter { it.friend.isFavorite == FriendEntity.Columns.YES }
                .map { it.asDomain(FriendListUiType.FavoriteItem()) }
        }
        //전체 리스트 분리
        val friendsUiList = async {
            friendsList.map { it.asDomain(FriendListUiType.FriendItem) }
        }
        val totalList = mutableListOf<FriendUiModel>()
        totalList.add(FriendUiHeader(title = "에이닷 친구들 ${adots.await().size}", uiId = FriendListUiType.Header().uiIdOffset + 1))
        totalList.addAll(adots.await())
        totalList.add(FriendUiHeader(title = "즐겨찾기 ${favorites.await().size}", uiId = FriendListUiType.Header().uiIdOffset + 2))
        totalList.addAll(favorites.await())
        totalList.add(FriendUiHeader(title = "전체 ${friendsUiList.await().size}", uiId = FriendListUiType.Header().uiIdOffset + 3))
        totalList.addAll(friendsUiList.await())
        totalList
    }

    //친구 목록을 가져온다.
//        friendsListFlow = friendRepository.getFriendsFlow(friendsSortOrder).toFriendModel()
//            .map {
//                FriendLoadSuccess(it) as FriendLoadingStatus
//            }.catch { cause -> emit(FriendLoadFailed(cause)) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = FRIENDS_FLOW_TIMEOUT),
//                initialValue = FriendLoading
//            )

}
