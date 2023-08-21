package dev.luteoos.simpleotp

import kotlin.test.Test
import kotlin.test.assertTrue

class HmacOTPTest {

    @Test
    fun run_HMAC(){
        val keyString = byteArrayOf(0x6A, 0x4A, 0x38, 0x2F, 0x87.toByte(), 0x1D,
            0xE5.toByte(), 0x56, 0xC2.toByte(), 0x4B).toString()
        val generator = HmacOTPGenerator(byteArrayOf(0x6A, 0x4A, 0x38, 0x2F, 0x87.toByte(), 0x1D,
            0xE5.toByte(), 0x56, 0xC2.toByte(), 0x4B))
        val otp = generator.generateOneTimePassword(1)
        assertTrue { 789456 == otp } //754029 result matching BWC implementation
    }
}