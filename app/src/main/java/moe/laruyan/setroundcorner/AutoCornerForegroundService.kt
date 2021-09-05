package moe.laruyan.setroundcorner

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.content.ContextCompat

class AutoCornerForegroundService : Service() {

    private var orientationChangeListener: OrientationChangeListener? = null
    private var startedForeground = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            AutoCornerLauncherService.ENTER_SERVICE -> {
                val isEnabled = intent.getBooleanExtra(AutoCornerLauncherService.SERVICE_ENABLED, true)
                val isPersistent = intent.getBooleanExtra(AutoCornerLauncherService.SERVICE_PERSISTENT, true)
                return handleActionEnter(applicationContext, isEnabled, isPersistent)
            }
            AutoCornerLauncherService.EXIT_SERVICE -> {
                return handleActionExit(applicationContext)
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {

        unregisterOclIfNotNull(applicationContext)

        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        // not implemented
        TODO("Return the communication channel to the service.")
    }


    private fun unregisterOclIfNotNull(context: Context) {
        if (orientationChangeListener != null) {
            orientationChangeListener!!.unregister(context)
            orientationChangeListener = null
        }
    }

    private fun handleActionEnter(context: Context, isEnabled: Boolean, isPersistent: Boolean): Int {
        unregisterOclIfNotNull(context)

        val intentFilter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        orientationChangeListener = OrientationChangeListener.getInstance(isEnabled, isPersistent)
        orientationChangeListener!!.register(context, intentFilter)
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        if (notificationManager != null) {
            createNotificationChannel(notificationManager)
            startForeground(NOTIFICATION_ID, createNotificationEntry())
            startedForeground = true
            return START_STICKY
        } else {
            handleActionExit(context)
            return START_NOT_STICKY
        }
    }

    private fun handleActionExit(context: Context): Int {
        unregisterOclIfNotNull(context)
        if (startedForeground) {
            stopForeground(true)
            startedForeground = false
        }
        stopSelf()
        return START_NOT_STICKY
    }


    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val autoCornerNotiChannel = NotificationChannel(CHANNEL_ID, getString(R.string.service_foreground_notichannel_name), NotificationManager.IMPORTANCE_MIN)
            autoCornerNotiChannel.enableVibration(false)
            autoCornerNotiChannel.enableLights(false)
            autoCornerNotiChannel.setSound(null, null)
            autoCornerNotiChannel.description = getString(R.string.service_foreground_notichannel_description)
            notificationManager.createNotificationChannel(autoCornerNotiChannel)
        }
    }

    private fun createNotificationEntry(): Notification {
        lateinit var notificationBuilder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = Notification.Builder(applicationContext, CHANNEL_ID)
        } else {
            notificationBuilder = Notification.Builder(applicationContext)
        }

        val launchMainActivityIntent = Intent(applicationContext, MainActivity::class.java)
        launchMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingMainActivityIntent = PendingIntent.getActivity(applicationContext, 0, launchMainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        notificationBuilder.setOngoing(true)
        notificationBuilder.setContentTitle(getString(R.string.service_foreground_name))
        notificationBuilder.setContentTitle(getString(R.string.service_foreground_description))
        notificationBuilder.setContentIntent(pendingMainActivityIntent)
        notificationBuilder.setWhen(SystemClock.currentThreadTimeMillis())

        return notificationBuilder.build()
    }

    companion object {
        private const val LOG_TAG = "AutoCornerForegroundService"

        private const val CHANNEL_ID = "moe.laruyan.setroundcorner.NOTI_CHANNEL.AUTO_CORNER_FOREGROUND_RUNNING"
        private const val NOTIFICATION_ID = 0xC0FFEE
    }
}