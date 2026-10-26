package com.privatereelstracker.util

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {

    private const val PREFS_NAME = "reel_tracker_prefs"
    private const val KEY_LOCK_ENABLED = "lock_feature_enabled"
    private const val KEY_AUTO_BLOCK_ENABLED = "auto_block_enabled"
    private const val KEY_NOTIF_FREQUENCY = "notification_frequency"

    private const val KEY_LIMIT_INSTAGRAM = "limit_instagram"
    private const val KEY_LIMIT_YOUTUBE = "limit_youtube"
    private const val KEY_LIMIT_FACEBOOK = "limit_facebook"
    private const val KEY_LIMIT_TIKTOK = "limit_tiktok"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isLockEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_LOCK_ENABLED, false)
    }

    fun setLockEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_LOCK_ENABLED, enabled).apply()
    }

    fun isAutoBlockEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_AUTO_BLOCK_ENABLED, isLockEnabled(context))
    }

    fun setAutoBlockEnabled(context: Context, enabled: Boolean) {
        setLockEnabled(context, enabled)
        getPrefs(context).edit().putBoolean(KEY_AUTO_BLOCK_ENABLED, enabled).apply()
    }

    fun getNotificationFrequency(context: Context): Int {
        return getPrefs(context).getInt(KEY_NOTIF_FREQUENCY, 10)
    }

    fun setNotificationFrequency(context: Context, frequency: Int) {
        getPrefs(context).edit().putInt(KEY_NOTIF_FREQUENCY, frequency.coerceAtLeast(1)).apply()
    }

    fun getAppLimit(context: Context, platform: String): Int {
        return when (platform) {
            "Instagram" -> getPrefs(context).getInt(KEY_LIMIT_INSTAGRAM, 30)
            "YouTube" -> getPrefs(context).getInt(KEY_LIMIT_YOUTUBE, 30)
            "Facebook" -> getPrefs(context).getInt(KEY_LIMIT_FACEBOOK, 20)
            "TikTok" -> getPrefs(context).getInt(KEY_LIMIT_TIKTOK, 20)
            else -> 50
        }
    }

    fun setAppLimit(context: Context, platform: String, limit: Int) {
        val safeLimit = limit.coerceAtLeast(1)
        val key = when (platform) {
            "Instagram" -> KEY_LIMIT_INSTAGRAM
            "YouTube" -> KEY_LIMIT_YOUTUBE
            "Facebook" -> KEY_LIMIT_FACEBOOK
            "TikTok" -> KEY_LIMIT_TIKTOK
            else -> return
        }
        getPrefs(context).edit().putInt(key, safeLimit).apply()
    }
}
