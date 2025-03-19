package cat.dam.mindspeak.firebase

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    fun saveEmail(email: String) {
        sharedPreferences.edit().putString("email", email).apply()
    }

    fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    fun savePassword(password: String) {
        sharedPreferences.edit().putString("password", password).apply()
    }

    fun getPassword(): String? {
        return sharedPreferences.getString("password", null)
    }

    fun saveRememberMe(rememberMe: Boolean) {
        sharedPreferences.edit().putBoolean("rememberMe", rememberMe).apply()
    }

    fun getRememberMe(): Boolean {
        return sharedPreferences.getBoolean("rememberMe", false)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}