package com.coslu.jobtracker

import androidx.compose.runtime.mutableStateOf

sealed class SortingMethod(val descending: Boolean) {
    companion object {
        private val state = mutableStateOf(fetchSortingMethod())
        var current
            get() = state.value
            set(value) {
                state.value = value
                jobs.sortWith(SortingMethod.current.comparator)
                saveSortingMethod(current)
            }
    }

    protected abstract val selector: (Job) -> Comparable<*>

    val comparator
        get() =
            if (descending) compareByDescending(selector).thenByDescending { it.date }
            else compareBy(selector).thenByDescending { it.date }

    override fun toString(): String {
        return "${javaClass.simpleName}($descending)"
    }

    class Date(descending: Boolean) : SortingMethod(descending) {
        override val selector: (Job) -> Comparable<*> = { it.date }
    }

    class Name(descending: Boolean) : SortingMethod(descending) {
        override val selector: (Job) -> Comparable<*> = { it.name.lowercase() }
    }

    class Type(descending: Boolean) : SortingMethod(descending) {
        override val selector: (Job) -> Comparable<*> = { it.type.lowercase() }
    }

    class Location(descending: Boolean) : SortingMethod(descending) {
        override val selector: (Job) -> Comparable<*> = { it.location.lowercase() }
    }

    class Status(descending: Boolean) : SortingMethod(descending) {
        override val selector: (Job) -> Comparable<*> = { it.status }
    }
}

expect fun saveSortingMethod(sortingMethod: SortingMethod)

expect fun fetchSortingMethod(): SortingMethod