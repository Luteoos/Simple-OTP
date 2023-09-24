# Simple-OTP
Kotlin Multiplatform library for OTP and TOTP password generation/validation

## Dependencies
- [MACs](https://github.com/KotlinCrypto/MACs)
- [KotlinX-DateTime](https://github.com/Kotlin/kotlinx-datetime)
- [Okio](https://github.com/square/okio) (`Buffer` implementation)
- [Encoding](https://github.com/05nelsonm/encoding) (`Base32` implementation)

## Installation

Add it in your root `build.gradle` at the end of repositories:
```kotlin

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add dependency to module `build.gradle`
```kotlin
dependencies {
		implementation("com.github.Luteoos:Simple-OTP:1.0.0")
	}
```

## Usage

### Initialize `TimeOTP` or `HMacOTP`

```kotlin
HMacOTP(ByteArrayKey)

//or when using non-standard Algorithm

HMacOTP(codeLength = 6, algorithm = Algorithm.HmacSHA256(ByteArrayKey))

//or if you have key as String

HMacOTP(secretKey, encoding = KeyEncoding.BASE32)

//or

TimeOTP(key = byteArrayKey, timestep = 60.seconds)
```

### Generate OTP

HOTP
```kotlin
generator.generateOneTimePassword(counter)
```

TOTP
```kotlin
generator.generateTimedOneTimePassword(epochMillis)

//or

generator.generateTimedOneTimePassword(time = timeInstant)
```

### Validate existing OTP using `isValid(StringOTP, counter)`

## License
[MIT](./LICENSE)