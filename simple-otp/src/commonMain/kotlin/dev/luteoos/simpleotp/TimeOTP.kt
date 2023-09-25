package dev.luteoos.simpleotp

import dev.luteoos.simpleotp.data.Algorithm
import dev.luteoos.simpleotp.data.KeyEncoding
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 *
 * Creates a new Time-based one-time password generator using the given password length and algorithm. Note that
 * [RFC&nbsp;4226](https://tools.ietf.org/html/rfc4226) and [RFC&nbsp;6238](https://tools.ietf.org/html/rfc6238) specifies that TOTP must always use HMAC-SHA1 as
 * an algorithm.
 *
 * @param codeLength the length, in decimal digits, of the one-time passwords to be generated;
 * @param algorithm [Algorithm] to use when generating passwords; by default uses [Algorithm.HmacSHA1]
 * @param timeStep [Duration] of totp lifecycle, recommended default at 30s
 *
 * @author Mateusz Lutecki
 */
class TimeOTP(codeLength: Int = TimeOTP.DEFAULT_PASSWORD_LENGTH, algorithm: Algorithm, private val timeStep: Duration = 30.seconds) {

    companion object {
        /**
         * Length for one-time passwords.
         */
        const val DEFAULT_PASSWORD_LENGTH = HMacOTP.DEFAULT_PASSWORD_LENGTH

        /**
         * Encode Key secret from [String] to [ByteArray] in UTF-8
         */
        fun encodeKey(secret: String, encoding: KeyEncoding = KeyEncoding.RAW): ByteArray {
            return HMacOTP.encodeKey(secret, encoding)
        }
    }

    constructor(codeLength: Int, key: ByteArray, timeStep: Duration = 30.seconds) :
        this(codeLength, algorithm = Algorithm.HmacSHA1(key), timeStep = timeStep)

    constructor(key: ByteArray, timeStep: Duration = 30.seconds) :
        this(algorithm = Algorithm.HmacSHA1(key), timeStep = timeStep)

    constructor(key: String, encoding: KeyEncoding = KeyEncoding.RAW, timeStep: Duration = 30.seconds) :
        this(DEFAULT_PASSWORD_LENGTH, key = key, encoding = encoding, timeStep = timeStep)

    constructor(codeLength: Int, key: String, encoding: KeyEncoding = KeyEncoding.RAW, timeStep: Duration = 30.seconds) :
        this(codeLength, key = encodeKey(key, encoding), timeStep = timeStep)

    private val hotp = HMacOTP(codeLength, algorithm)

    fun generateTimedOneTimePassword(epochMillis: Long): Int {
        return hotp.generateOneTimePassword(getTimeCounter(epochMillis = epochMillis))
    }

    fun generateTimedOneTimePassword(time: Instant): Int {
        return generateTimedOneTimePassword(epochMillis = time.toEpochMilliseconds())
    }

    fun isValid(incomingTotp: String, epochMillis: Long): Boolean {
        return incomingTotp == generateTimedOneTimePassword(epochMillis).toString()
    }

    fun isValid(incomingTotp: String, time: Instant): Boolean {
        return isValid(incomingTotp, time.toEpochMilliseconds())
    }

    private fun getTimeCounter(epochMillis: Long): Long = epochMillis.floorDiv(timeStep.inWholeMilliseconds)
}
