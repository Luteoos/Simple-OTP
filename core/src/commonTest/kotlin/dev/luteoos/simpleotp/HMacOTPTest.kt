package dev.luteoos.simpleotp

import dev.luteoos.simpleotp.data.KeyEncoding
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertTrue

class HMacOTPTest {

    @Test
    fun test_byteArray_HMAC() {
        val generator = HMacOTP(
            byteArrayOf(
                0x6A, 0x4A, 0x38, 0x2F, 0x87.toByte(), 0x1D,
                0xE5.toByte(), 0x56, 0xC2.toByte(), 0x4B
            )
        )
        val otp = generator.generateOneTimePassword(1)
        assertTrue { 754029 == otp } // 754029 result matching BWC implementation
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun test_string_HMAC() {
        val keyString = byteArrayOf(
            0x6A, 0x4A, 0x38, 0x2F, 0x87.toByte(), 0x1D,
            0xE5.toByte(), 0x56, 0xC2.toByte(), 0x4B
        ).toString()
        val generator = HMacOTP("test")
        val otp = generator.generateOneTimePassword(1)
        assertTrue { 431881 == otp } // 431881 for utf8 encoded
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun test_stringBase32_HMAC() {
        val generator = HMacOTP("LSHCW3NOMUT4JRCPA4DIZ5SDIUWU26I3WDRYJU43K2TNH2BZ7HGTYWRXUXY24OQQZO6ZUKH5GSY6RI2RIQCHFDZKOQDQLY7DT42463I", encoding = KeyEncoding.BASE32)
        val otp = generator.generateOneTimePassword(1)
        assertTrue { 991304 == otp }
    }
}
