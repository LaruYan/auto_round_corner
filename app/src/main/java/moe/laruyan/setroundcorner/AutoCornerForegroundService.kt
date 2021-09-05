package moe.laruyan.setroundcorner

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

class AutoCornerForegroundService() : Service() {

    private var orientationChangeListener: OrientationChangeListener? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            AutoCornerLauncherService.ENTER_SERVICE -> {
                val isEnabled = intent.getBooleanExtra(AutoCornerLauncherService.SERVICE_ENABLED, true)
                val isPersistent = intent.getBooleanExtra(AutoCornerLauncherService.SERVICE_PERSISTENT, true)
                handleActionEnter(applicationContext, isEnabled, isPersistent)
            }
            AutoCornerLauncherService.EXIT_SERVICE -> {
                handleActionExit(applicationContext)
            }
        }

        return START_STICKY
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

    private fun handleActionEnter(context: Context, isEnabled: Boolean, isPersistent: Boolean) {
        unregisterOclIfNotNull(context)

        val intentFilter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        orientationChangeListener = OrientationChangeListener.getInstance(isEnabled, isPersistent)
        orientationChangeListener!!.register(context, intentFilter)
    }

    private fun handleActionExit(context: Context) {
        unregisterOclIfNotNull(context)
        stopSelf()
    }

    companion object {
        private const val LOG_TAG = "AutoCornerForegroundService"
    }
}