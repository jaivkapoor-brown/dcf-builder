package com.jai.dcfbuilder

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform