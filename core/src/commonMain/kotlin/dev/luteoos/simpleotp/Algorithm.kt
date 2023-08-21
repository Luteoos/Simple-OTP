package dev.luteoos.simpleotp

import org.kotlincrypto.core.mac.Mac


sealed class Algorithm(val mac: Mac){
    class HmacSHA1(key: ByteArray = byteArrayOf(0x00)) : Algorithm(org.kotlincrypto.macs.hmac.sha1.HmacSHA1(key))
    class HmacSHA256(key: ByteArray = byteArrayOf(0x00)) : Algorithm(org.kotlincrypto.macs.hmac.sha2.HmacSHA256(key))
    class HmacSHA512(key: ByteArray = byteArrayOf(0x00)) : Algorithm(org.kotlincrypto.macs.hmac.sha2.HmacSHA512(key))
}
