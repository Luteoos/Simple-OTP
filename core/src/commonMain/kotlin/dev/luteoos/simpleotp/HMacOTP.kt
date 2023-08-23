@file:OptIn(ExperimentalEncodingApi::class, ExperimentalEncodingApi::class)

package dev.luteoos.simpleotp

import io.matthewnelson.encoding.base32.Base32Default
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import okio.Buffer
import org.kotlincrypto.core.mac.Mac
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.pow

/**
 *
 * Creates a new HMAC-based one-time password generator using the given password length and algorithm. Note that
 * [RFC&nbsp;4226](https://tools.ietf.org/html/rfc4226) specifies that HOTP must always use HMAC-SHA1 as
 * an algorithm, but derived one-time password systems like TOTP may allow for other algorithms.
 *
 * @param codeLength the length, in decimal digits, of the one-time passwords to be generated;
 * @param algorithm [Algorithm] to use when generating passwords; by default uses [Algorithm.HmacSHA1]
 *
 * @author Mateusz Lutecki
 */
class HMacOTP(private val codeLength: Int = DEFAULT_PASSWORD_LENGTH, private val algorithm: Algorithm) {

    companion object {
        /**
         * Length for one-time passwords.
         */
        const val DEFAULT_PASSWORD_LENGTH = 6

        /**
         * Encode Key secret from [String] to [ByteArray] in UTF-8
         */
        fun encodeKey(secret: String, encoding: KeyEncoding = KeyEncoding.RAW): ByteArray {
            return when (encoding) {
                KeyEncoding.RAW -> secret.encodeToByteArray()
                KeyEncoding.BASE32 -> secret.decodeToByteArray(Base32Default())
                KeyEncoding.BASE64 -> Base64.decode(secret)
            }
        }
    }

    constructor(codeLength: Int, key: ByteArray) : this(codeLength, algorithm = Algorithm.HmacSHA1(key))

    constructor(key: ByteArray) : this(algorithm = Algorithm.HmacSHA1(key))

    constructor(key: String, encoding: KeyEncoding = KeyEncoding.RAW) : this(DEFAULT_PASSWORD_LENGTH, key = key, encoding = encoding)

    constructor(codeLength: Int, key: String, encoding: KeyEncoding = KeyEncoding.RAW) : this(codeLength, key = HMacOTP.encodeKey(key, encoding))

    fun generateOneTimePassword(counter: Long): Int {
        val mac = getMac()
        val buffer = Buffer().writeLong(counter).readByteArray()
        val macOutput = mac.doFinal(buffer)
        val offset = macOutput.last().toInt() and 0x0f
        val out = Buffer()
        Buffer().write(macOutput).copyTo(out, offset.toLong(), 4)
        return (out.readInt() and 0x7fffffff) % 10.0.pow(codeLength).toInt()
    }

    fun isValid(incomingOtp: String, counter: Long): Boolean =
        incomingOtp == generateOneTimePassword(counter).toString()

    private fun getMac(): Mac = algorithm.mac

    fun getPasswordLength(): Int = codeLength

    fun getAlgorithm(): String = algorithm.mac.algorithm()
}
