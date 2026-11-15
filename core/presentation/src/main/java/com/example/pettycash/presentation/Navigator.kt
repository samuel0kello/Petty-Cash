package com.example.pettycash.presentation

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavBackStack

class Navigator(private val backStack: NavBackStack<NavKey>) {

    fun goTo(key: NavKey) {
        backStack.add(key)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun replaceAll(key: NavKey) {
        backStack.clear()
        backStack.add(key)
    }

    fun replaceCurrent(key: NavKey) {
        if (backStack.isNotEmpty()) {
            backStack[backStack.lastIndex] = key
        } else {
            backStack.add(key)
        }
    }
}
