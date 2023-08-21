package dev.luteoos.simpleotp

import okio.Buffer
import org.kotlincrypto.core.mac.Mac

/**
 *
 * Creates a new HMAC-based one-time password generator using the given password length and algorithm. Note that
 * [RFC&nbsp;4226](https://tools.ietf.org/html/rfc4226) specifies that HOTP must always use HMAC-SHA1 as
 * an algorithm, but derived one-time password systems like TOTP may allow for other algorithms.
 *
 * @param passwordLength the length, in decimal digits, of the one-time passwords to be generated; must be between
 * 6 and 8, inclusive
 * @param algorithm the name of the [javax.crypto.Mac] algorithm to use when generating passwords; note that
 * HOTP only allows for {@value HmacOTPGenerator#HOTP_HMAC_ALGORITHM}, but derived
 * standards like TOTP may allow for other algorithms
 *
 * @throws NoAlgorithmException if the given algorithm is not supported by the underlying JRE
 */
class HmacOTPGenerator(passwordLength: Int = DEFAULT_PASSWORD_LENGTH, private val algorithm: Algorithm) {

    private var passwordLength = DEFAULT_PASSWORD_LENGTH

    private var modDivisor = 0

    private var formatString: String = "%06d"

    companion object {
        /**
         * The default length, in decimal digits, for one-time passwords.
         */
        val DEFAULT_PASSWORD_LENGTH = 6
    }

    constructor(passwordLength: Int, key: ByteArray): this(passwordLength, algorithm = Algorithm.HmacSHA1(key))

    constructor(key: ByteArray): this(algorithm = Algorithm.HmacSHA1(key))

    init {
        when (passwordLength) {
            6 -> {
                modDivisor = 1000000
                formatString = "%06d"
            }

            7 -> {
                modDivisor = 10000000
                formatString = "%07d"
            }

            8 -> {
                modDivisor = 100000000
                formatString = "%08d"
            }
            else -> {
                throw IllegalArgumentException("Password length must be between 6 and 8 digits.")
            }
        }
        this.passwordLength = passwordLength
//        this.algorithm.mac.update(byteArrayOf(0x00), 0, passwordLength)
    }

    fun generateOneTimePassword(counter: Long): Int {
        val mac = getMac()
        val buffer = Buffer().writeLong(counter)
//        buffer.write(Buffer().write(ByteArray(mac.macLength()) { 0x00 }), mac.macLength().toLong() - 8)
//        buffer.writeLong(counter) //probqably reverse
//        try {
            val array = buffer.readByteArray()
//            mac.update(array, 0, 8)
            val output = mac.doFinal(array)
            val offset = output.last().toInt() and 0x0f
            val out = Buffer()
            Buffer().write(output).copyTo(out, offset.toLong(), 4)
            return (out.readInt() and 0x7fffffff) % modDivisor
//        } catch (e: Exception) {
//            throw Exception(e)
//        }
    }

    private fun getMac(): Mac {
        return algorithm.mac
    }
//    fun generateOneTimePasswordString(key: Key?, counter: Long): String? {
//        return this.generateOneTimePasswordString(key, counter)
//    }
//
//    fun generateOneTimePasswordString(keyByteArray: ByteArray, counter: Long): String? {
//        return this.generateOneTimePasswordString(generateKey(keyByteArray), counter)
//    }
//
//    fun formatOneTimePassword(oneTimePassword: Int): String {
//        return String.format(formatString, oneTimePassword)
//    }

    fun getPasswordLength(): Int = passwordLength

    fun getAlgorithm(): String = algorithm.mac.algorithm()

//    fun generateKey(key: ByteArray): Key? {
//        try {
//            return SecretKeySpec(key, "RAW")
//        } catch (e: IllegalArgumentException) {
//            Timber.tag("HMAC-OTP").e("generateKey -  %s", e.message)
//        }
//        return null
//    }
}