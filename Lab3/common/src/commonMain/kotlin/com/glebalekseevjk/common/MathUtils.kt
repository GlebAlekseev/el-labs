package com.glebalekseevjk.common

fun Double.roundToString() = "%.3f".format(this).replace(Regex("\\,*0+$"), "")