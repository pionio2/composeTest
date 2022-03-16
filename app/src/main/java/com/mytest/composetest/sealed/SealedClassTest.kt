package com.mytest.composetest.sealed

import com.mytest.composetest.MainButton

sealed class SealedClassTest {
    object AObject: SealedClassTest()
    data class BDataClass(val text: String): SealedClassTest()
    object CObject: SealedClassTest()
    sealed class DSealedClass: SealedClassTest() {
        object EObject: DSealedClass()
        object FObject: DSealedClass()
    }
}