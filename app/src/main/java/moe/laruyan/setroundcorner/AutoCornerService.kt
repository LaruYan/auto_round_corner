package moe.laruyan.setroundcorner

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.content.IntentFilter

private const val ENTER_SERVICE = "moe.laruyan.setroundcorner.action.ENTER_SERVICE"
private const val EXIT_SERVICE = "moe.laruyan.setroundcorner.action.EXIT_SERVICE"

// TODO: Rename parameters
private const val SERVICE_ENABLED = "moe.laruyan.setroundcorner.extra.SERVICE_ENABLED"
private const val SERVICE_PERSISTENT = "moe.laruyan.setroundcorner.extra.SERVICE_PERSISTENT"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.

 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.

 */
class AutoCornerService : IntentService("AutoCornerService") {

    private var orientationChangeListener: OrientationChangeListener? = null

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
        unregisterOclIfNotNull(context)

        val intentFilter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        orientationChangeListener = OrientationChangeListener.getInstance(isEnabled, isPersistent)
        orientationChangeListener!!.register(context, intentFilter)
    }

    /**
     * Handle action Exit in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionExit(context: Context) {
        unregisterOclIfNotNull(context)
        stopSelf()
    }

    private fun unregisterOclIfNotNull(context: Context) {
        if (orientationChangeListener != null) {
            orientationChangeListener!!.unregister(context)
            orientationChangeListener = null
        }
    }

    companion object {
        /**
         * Starts this service to perform action Enter with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        @JvmStatic
        fun startActionEnter(context: Context, isEnabled: Boolean, isPersistent: Boolean) {
            val intent = Intent(context, AutoCornerService::class.java).apply {
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
            val intent = Intent(context, AutoCornerService::class.java).apply {
                action = EXIT_SERVICE
            }
            context.startService(intent)
        }
    }
}