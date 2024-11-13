package ru.shvetsov.memehub.data.network.token

import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject
import javax.inject.Inject

class TokenStorage @Inject constructor(
    val sharedPreferences: SharedPreferences,
) {
    fun saveToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun isTokenExpired(token: String): Boolean {
        val parts = token.split(".")

        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token")
        }

        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING))
        val json = JSONObject(payload)

        val exp = json.optLong("exp", 0) * 1000
        return exp < System.currentTimeMillis()
    }

    fun saveUserId(id: Int) {
        sharedPreferences.edit().putInt("id", id).apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt("id", 1)
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }
}