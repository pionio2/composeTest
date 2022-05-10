package com.mytest.composetest.sealed

import com.mytest.composetest.MainActivity
import com.mytest.composetest.MainButton
import com.mytest.composetest.util.LogError

sealed class SealedClassTest {
    val TAG = "SealedClassTest"
    object AObject: SealedClassTest()
    data class BDataClass(val text: String): SealedClassTest()
    object CObject: SealedClassTest()
    sealed class DSealedClass: SealedClassTest() {
        object EObject: DSealedClass()
        object FObject: DSealedClass()
    }

    private fun test() {
        val list1 = SealedClassTest::class.sealedSubclasses
        val list2 = SealedClassTest::class.sealedSubclasses.mapNotNull { it.objectInstance }
        LogError(TAG) {"list1:$list1"}
        LogError(TAG) {"list2:$list2"}

        val list3 = MainButton::class.sealedSubclasses
        LogError(TAG) {"list3:$list3"}
    }

    private fun processResult(result: Result1<String>) {
        result.onSuccess {data -> LogError(TAG){"doo:sucess $data"} }
        result.onFailed { exception ->  LogError(TAG) {"doo:error $exception"} }
        result.onProgress { LogError(TAG) {"doo:progress"}  }
    }
}

sealed class Result1<out T : Any> {
    data class Success<out T: Any>(val data: T) : Result1<T>()
    data class Failed(val exception: Exception) : Result1<Nothing>()
    object InProgress : Result1<Nothing>()

    fun onFailed(action: (exception: Exception) -> Unit) {
        if (this is Failed) {
            action(exception)
        }
    }

    fun onSuccess(action: (data: T) -> Unit) {
        if (this is Success) {
            action(data)
        }
    }

    fun onProgress(action: () -> Unit) {
        if (this is InProgress) {
            action()
        }
    }

    fun <R : Any> map(transform: (T) -> R): Result1<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Failed -> this
            is InProgress -> this
        }
    }
}