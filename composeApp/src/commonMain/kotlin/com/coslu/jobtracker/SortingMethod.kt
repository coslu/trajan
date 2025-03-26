package com.coslu.jobtracker

sealed class SortingMethod(val descending: Boolean) {
    protected abstract val selector: (Job) -> Comparable<*>

    open val comparator
        get() = (if (descending) compareByDescending(selector) else compareBy(selector))
            .then(Date(true).comparator)

    override fun toString(): String {
        return "${javaClass.simpleName}($descending)"
    }

    class Date(descending: Boolean) : SortingMethod(descending) {
        override val selector: (Job) -> Comparable<*> = { it.date }
        override val comparator =
            if (descending) compareByDescending(selector).thenBy { it.hashCode() }
            else compareBy(selector).thenByDescending { it.hashCode() }
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