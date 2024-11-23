package com.coslu.jobtracker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform