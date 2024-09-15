package io.github.rajeev02.kmpapp001

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMPApp001",
    ) {
        App()
    }
}