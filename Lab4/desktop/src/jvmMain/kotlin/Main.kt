// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.glebalekseevjk.common.App
import java.awt.Dimension

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Лабораторная работа 4", resizable = true) {
        window.minimumSize = Dimension(990, 660)
        App()
    }
}