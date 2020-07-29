package com.kgdeveloping.securenote

import android.content.Context
import android.util.Log
import java.nio.charset.Charset
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/*
Class to call to encrypt a plaintext given a password
    and decrypt a cipherText given its Salt and IV and a password
 */

class Encrypt(private val context: Context) {

    fun encrypt(plaintext:String, pass:String): HashMap<String, ByteArray>{
        Log.d("Encrypt.encrypt", "encrypt called")

        if (pass.isEmpty()){
            throw Exception(context.getString(R.string.encrypt_pass_empty))
        }
        val password = pass.toCharArray()

        //Generate salt
        val rand = SecureRandom()
        val salt = ByteArray(256)
        rand.nextBytes(salt)

        //Generate IV
        val ivRand = SecureRandom()
        val iv = ByteArray(16)
        ivRand.nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        //generate key from password
        val pbKeySpec = PBEKeySpec(password, salt, 1324, 256)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
        val keySpec = SecretKeySpec(keyBytes, "AES")

        //encrypt plaintext
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val cipherText = cipher.doFinal(plaintext.toByteArray(Charset.defaultCharset()))

        //package and return values to store
        val map = HashMap<String, ByteArray>()
        map["Salt"] = salt
        map["IV"] = iv
        map["Cipher"] = cipherText
        return map
    }

    fun decrypt(info:HashMap<String, ByteArray>, pass:String): String{
        Log.d("Encrypt.decrypt", "decrypt called")

        if (pass.isEmpty()){
            throw Exception(context.getString(R.string.decrypt_pass_empty))
        }
        val password = pass.toCharArray()

        //Get salt and IV from the map
        val salt = info["Salt"]
        val iv = info["IV"]
        val cipherText = info["Cipher"]

        //regenerate key from password
        val pbKeySpec = PBEKeySpec(password, salt, 1324, 256)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
        val keySpec = SecretKeySpec(keyBytes, "AES")

        //decrypt plaintext
        val ivSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decryptText = cipher.doFinal(cipherText)

        return decryptText.toString(Charset.defaultCharset())
    }

}