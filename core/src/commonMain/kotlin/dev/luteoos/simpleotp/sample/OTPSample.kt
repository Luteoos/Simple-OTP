package dev.luteoos.simpleotp.sample

import dev.luteoos.simpleotp.HMacOTP
import dev.luteoos.simpleotp.TimeOTP
import dev.luteoos.simpleotp.data.Algorithm
import dev.luteoos.simpleotp.data.KeyEncoding
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

class OTPSample {

    fun getHOTPFromByteArray() {
        val key = byteArrayOf(0x00, 0x01, 0x03)
        val counter = 69L

        val generator = HMacOTP(key)
        val otp = generator.generateOneTimePassword(counter)
        generator.isValid(otp.toString(), counter)
    }

    fun getHOTPSHA256FromByteArray() {
        val key = byteArrayOf(0x00, 0x01, 0x03)
        val counter = 69L

        val generator = HMacOTP(algorithm = Algorithm.HmacSHA256(key))
        val otp = generator.generateOneTimePassword(counter)
        generator.isValid(otp.toString(), counter)
    }

    fun getHOTPFromBase32String() {
        val secret = "QWERTYZ"
        val counter = 69L

        val generator = HMacOTP(HMacOTP.encodeKey(secret, encoding = KeyEncoding.BASE32))
        val otp = generator.generateOneTimePassword(counter)
        generator.isValid(otp.toString(), counter)
    }

    fun getTOTPFromByteArray() {
        val key = byteArrayOf(0x00, 0x01, 0x03)
        val timeInstant = Clock.System.now()

        val generator = TimeOTP(key)
        val otp = generator.generateTimedOneTimePassword(time = timeInstant)
        generator.isValid(otp.toString(), timeInstant)
    }

    fun getTOTPSHA256FromByteArray() {
        val key = byteArrayOf(0x00, 0x01, 0x03)
        val epochMillis = 69L

        val generator = TimeOTP(algorithm = Algorithm.HmacSHA256(key))
        val otp = generator.generateTimedOneTimePassword(epochMillis)
        generator.isValid(otp.toString(), epochMillis)
    }

    fun getTOTPFromBase32String() {
        val secret = "QWERTYZ"
        val epochMillis = 69L
        val generator = TimeOTP(TimeOTP.encodeKey(secret, encoding = KeyEncoding.BASE32))
        val otp = generator.generateTimedOneTimePassword(epochMillis)
        generator.isValid(otp.toString(), Clock.System.now())
    }

    fun getTOTPFromRawString() {
        val secret = "QWERTYZ"
        val epochMillis = 69L

        val generator = TimeOTP(
            codeLength = 7,
            secret,
            encoding = KeyEncoding.RAW,
            timeStep = 60.seconds
        )
        val otp = generator.generateTimedOneTimePassword(epochMillis)
        generator.isValid(otp.toString(), Clock.System.now())
    }
}
