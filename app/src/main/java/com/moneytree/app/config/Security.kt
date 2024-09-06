package com.moneytree.app.config

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Security {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val KEY_SIZE = 256
    private const val IV_SIZE = 16
//poOtBNUmOwydFoDBVnHWlknxz8DjxCrr2CZzeXgF04E=
    // Function to generate a new SecretKey
    private fun generateSecretKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance(ALGORITHM)
        keyGen.init(KEY_SIZE)
        return keyGen.generateKey()
    }

    // Function to convert SecretKey to String
    fun secretKeyToString(): String {
        val secretKey = generateSecretKey()
        return Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
    }

    // Function to convert String back to SecretKey
    fun stringToSecretKey(secretKeyString: String): SecretKey {
        val decodedKey = Base64.decode(secretKeyString, Base64.DEFAULT)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, ALGORITHM)
    }

    // Encrypts the input string and returns a Base64 encoded string
    fun encrypt(input: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(IV_SIZE).apply { SecureRandom().nextBytes(this) }
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encryptedBytes = cipher.doFinal(input.toByteArray())
        val ivAndCipherText = iv + encryptedBytes
        return Base64.encodeToString(ivAndCipherText, Base64.DEFAULT)
    }

    // Decrypts the Base64 encoded string and returns the original string
    fun decrypt(encryptedInput: String, secretKey: SecretKey): String {
        val decodedBytes = Base64.decode(encryptedInput, Base64.DEFAULT)
        val iv = decodedBytes.copyOfRange(0, IV_SIZE)
        val cipherText = decodedBytes.copyOfRange(IV_SIZE, decodedBytes.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        val decryptedBytes = cipher.doFinal(cipherText)
        return String(decryptedBytes)
    }
}