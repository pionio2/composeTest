package com.mytest.composetest.util

import android.util.Log

/**
 * kotlin에서 사용하기 위한 utility functions
 */

const val APP_TAG = "MyTestApp"

inline fun LogDebug(TAG: String, printLog: () -> String) {
    Log.d(APP_TAG, "[$TAG] ${printLog()}")
}

inline fun LogInfo(TAG: String, printLog: () -> String) {
    Log.i(APP_TAG, "[$TAG] ${printLog()}")
}

inline fun LogWarn(TAG: String, e: Exception? = null, printLog: () -> String) {
    if (e == null) {
        Log.w(APP_TAG, "[$TAG] ${printLog()}")
    } else {
        Log.w(APP_TAG, "[$TAG] ${printLog()}", e)
    }
}

inline fun LogError(TAG: String, e: Exception? = null, printLog: () -> String) {
    if (e == null) {
        Log.e(APP_TAG, "[$TAG] ${printLog()}")
    } else {
        Log.e(TAG, "[$TAG] ${printLog()}", e)
    }
}
