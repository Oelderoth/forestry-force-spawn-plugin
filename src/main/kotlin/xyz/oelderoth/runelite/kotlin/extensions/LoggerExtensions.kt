package xyz.oelderoth.runelite.kotlin.extensions

import org.slf4j.LoggerFactory

fun Any.getLogger() = LoggerFactory.getLogger(this::class.java)