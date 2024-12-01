package ru.shvetsov.memehub.data.network.token

import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject
import ru.shvetsov.memehub.utils.constants.Constants.ID
import ru.shvetsov.memehub.utils.constants.Constants.INVALID_JWT_TOKEN
import ru.shvetsov.memehub.utils.constants.Constants.TOKEN
import javax.inject.Inject

class TokenStorage @Inject constructor(
    val sharedPreferences: SharedPreferences
) {
    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN, token).apply()
    }

    fun isTokenExpired(token: String): Boolean {
        val parts = token.split(".")

        if (parts.size != 3) {
            throw IllegalArgumentException(INVALID_JWT_TOKEN)
        }

        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING))
        val json = JSONObject(payload)

        val exp = json.optLong("exp", 0) * 1000
        return exp < System.currentTimeMillis()
    }

    fun saveUserId(id: Int) {
        sharedPreferences.edit().putInt(ID, id).apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(ID, 1)
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN, null)
    }
}