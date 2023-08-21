package dev.luteoos.simpleotp.sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform