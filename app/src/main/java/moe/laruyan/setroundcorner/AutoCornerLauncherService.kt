package moe.laruyan.setroundcorner

import android.app.*
import android.content.Intent
import android.content.Context
import android.os.Build

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.

 * helper methods.

 */
class AutoCornerLauncherService : IntentService("AutoCornerService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ENTER_SERVICE -> {
                val isEnabled = intent.getBooleanExtra(SERVICE_ENABLED, true)
                val isPersistent = intent.getBooleanExtra(SERVICE_PERSISTENT, true)
                handleActionEnter(applicationContext, isEnabled, isPersistent)
            }
            EXIT_SERVICE -> {
                handleActionExit(applicationContext)
            }
        }

    }

    /**
     * Handle action Enter in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionEnter(context: Context, isEnabled: Boolean, isPersistent: Boolean) {
        val autoCornerServiceEnterIntent = Intent(context, AutoCornerForegroundService::class.java).apply {
            action = ENTER_SERVICE
            putExtra(SERVICE_ENABLED, isEnabled)
            putExtra(SERVICE_PERSISTENT, isPersistent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(autoCornerServiceEnterIntent)
        } else {
            context.startService(autoCornerServiceEnterIntent)
        }
    }

    /**
     * Handle action Exit in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionExit(context: Context) {
        val autoCornerServiceExitIntent = Intent(context, AutoCornerForegroundService::class.java).apply {
            action = EXIT_SERVICE
        }
        context.stopService(autoCornerServiceExitIntent)
    }

    companion object {
        private const val LOG_TAG = "AutoCornerLauncherService"

        const val ENTER_SERVICE = "moe.laruyan.setroundcorner.action.ENTER_SERVICE"
        const val EXIT_SERVICE = "moe.laruyan.setroundcorner.action.EXIT_SERVICE"

        const val SERVICE_ENABLED = "moe.laruyan.setroundcorner.extra.SERVICE_ENABLED"
        const val SERVICE_PERSISTENT = "moe.laruyan.setroundcorner.extra.SERVICE_PERSISTENT"

        /**
         * Starts this service to perform action Enter with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        @JvmStatic
        fun startActionEnter(context: Context, isEnabled: Boolean, isPersistent: Boolean) {
            val intent = Intent(context, AutoCornerLauncherService::class.java).apply {
                action = ENTER_SERVICE
                putExtra(SERVICE_ENABLED, isEnabled)
                putExtra(SERVICE_PERSISTENT, isPersistent)
            }
            context.startService(intent)
        }
        /**
         * Stops this service to perform action Exit with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        @JvmStatic
        fun startActionExit(context: Context) {
            val intent = Intent(context, AutoCornerLauncherService::class.java).apply {
                action = EXIT_SERVICE
            }
            context.startService(intent)
        }
    }
}