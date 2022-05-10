package com.mytest.composetest.coroutinetest

import androidx.lifecycle.asLiveData
import com.mytest.composetest.util.LogDebug
import com.mytest.composetest.util.LogError
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

class CallbackFlowTest : CoroutineScope {
    private val TAG = "CallbackFlowTest"

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main

    data class TestText(var msg: String)

    private fun getCallbackFlowTest(): Flow<String> {
        return callbackFlow {

            repeat(10000) {
                delay(100)
                trySend("body thread:${Thread.currentThread().name} - $it")
            }

            awaitClose {
                //
            }
        }
            .map {
                delay(1000)
                "map thread:${Thread.currentThread().name} | $it"
            }
            .conflate()
            .flowOn(Dispatchers.IO)
    }

    suspend fun test() {
        getCallbackFlowTest().collect {
            delay(2000)
            LogError(TAG) { it }
        }
    }

    fun test2() = getCallbackFlowTest().asLiveData()

    private fun initForPreload() = callbackFlow {
        //...
        trySend {
            1 // 임시로 넣은값
        }
        awaitClose {
            //...
        }
    }
        .conflate()
        .map {
            it //기존 map 내용 추가
        }
        .flowOn(Dispatchers.IO) //Dispatchers.IO 대신 keyDefaultCoroutineDispatcher 사용
        .asLiveData()


    private fun initForDownloadable() {
        //...
        callbackFlow {
            //...
            trySend {
                1 // 임시로 넣은값
            }
            awaitClose {
                //...
            }
        }
            .conflate()
            .onEach {
                //map의 내용 + collect에 있던 내용
            }
            .launchIn(this + Dispatchers.IO) //Dispatchers.IO 대신 keyDefaultCoroutineDispatcher 사용

        // Transformation.map 을 return
    }

    fun callbackFlowTest2() {
        LogDebug(TAG) { "callbackFlowTest2() - START" }
        callbackFlow {

            repeat(10000) {
                delay(100)
                trySend("body thread:${Thread.currentThread().name} - $it")
            }

            awaitClose {
                //
            }
        }
            .conflate()
            .onEach {
                delay(1000)
                LogError(TAG) { "map thread:${Thread.currentThread().name} | $it" }
            }
            .launchIn(this + Dispatchers.IO)
        LogDebug(TAG) { "callbackFlowTest2()- END" }
    }

    suspend fun getCallbackFlowTest4() {
        callbackFlow<TestText> {

            val testText = TestText("start")
            repeat(10000) {
                delay(100)
                val msg = "body thread:${Thread.currentThread().name} - $it"
//                testText.msg = msg
//                trySend(testText)

                trySend(TestText("aa"))
            }

            awaitClose {
                //
            }
        }
            .distinctUntilChanged()
            .collect {
                LogError(TAG) {it.msg}
            }
    }
}

