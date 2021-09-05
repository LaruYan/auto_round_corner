package moe.laruyan.setroundcorner

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_REBOOT, // Xiaomi
            "com.htc.action.QUICKBOOT_POWERON", // htc
            "android.intent.action.QUICKBOOT_POWERON", // somewhere I forgot
            -> {
                if (context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                    AutoCornerLauncherService.startActionEnter(context, true, true)
                }
            }
        }
    }
}