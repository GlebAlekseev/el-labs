package com.glebalekseevjk.common

fun Double.roundToString(n: Int) = "%.${n}f".format(this).replace(Regex("\\,*.*0+$"), "")

fun Double.roundUp(): Double = if (this - this.toInt() == 0.0) this else this.toInt() + 1.0