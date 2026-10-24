package com.privatereelstracker.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.privatereelstracker.service.ReelsAccessibilityService
import com.privatereelstracker.tracker.UsageStatsHelper

object PermissionHelper {

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        if (ReelsAccessibilityService.isRunning) return true

        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            ?: return false
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        val expectedServiceName = "${context.packageName}/${ReelsAccessibilityService::class.java.name}"

        return enabledServices.any {
            it.id.equals(expectedServiceName, ignoreCase = true) ||
            it.resolveInfo.serviceInfo.packageName == context.packageName
        }
    }

    fun isUsageAccessEnabled(context: Context): Boolean {
        return UsageStatsHelper.hasUsagePermission(context)
    }

    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openUsageAccessSettings(context: Context) {
        val intent = UsageStatsHelper.createUsageAccessIntent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
