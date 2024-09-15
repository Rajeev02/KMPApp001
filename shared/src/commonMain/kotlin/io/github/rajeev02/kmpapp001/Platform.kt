package io.github.rajeev02.kmpapp001

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform