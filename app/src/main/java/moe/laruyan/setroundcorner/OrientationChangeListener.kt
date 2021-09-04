package moe.laruyan.setroundcorner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.content.IntentFilter
import java.lang.IllegalStateException


class OrientationChangeListener() : BroadcastReceiver() {
    private var isEnabled = true
    private var isPersistent = true
    private var isRegistered = false
    private var currentOrientation: Int? = null

    constructor (
        isEnabled: Boolean,
        isPersistent: Boolean,
    ) : this() {
        this.isEnabled = isEnabled
        this.isPersistent = isPersistent
    }
    /**
     * register receiver
     * @param context - Context
     * @param filter - Intent Filter
     * @return see Context.registerReceiver(BroadcastReceiver,IntentFilter)
     */
    fun register(context: Context, filter: IntentFilter?): Intent? {
        return try {
            // https://stackoverflow.com/a/29836639
            // ceph3us note:
            // here I propose to create
            // a isRegistered(Contex) method
            // as you can register receiver on different context
            // so you need to match against the same one :)
            // example  by storing a list of weak references
            // see LoadedApk.class - receiver dispatcher
            // its and ArrayMap there for example
            if (!isRegistered) context.registerReceiver(this, filter) else null
        } finally {
            isRegistered = true
        }
    }

    /**
     * unregister received
     * @param context - context
     * @return true if was registered else false
     */
    fun unregister(context: Context): Boolean {
        // https://stackoverflow.com/a/29836639
        // additional work match on context before unregister
        // eg store weak ref in register then compare in unregister
        // if match same instance
        return (isRegistered
                && unregisterInternal(context))
    }

    private fun unregisterInternal(context: Context): Boolean {
        // https://stackoverflow.com/a/29836639
        try {
            context.unregisterReceiver(this)
        } catch (ise: IllegalStateException) {
            // already unregistered or failed to unregister
            return false
        } finally {
            isRegistered = false
        }
        return true
    }

    override fun onReceive(context: Context, intent: Intent) {
        var actionNecessary  = false
        val newOrientation: Int = context.resources.configuration.orientation

        if (currentOrientation == null) {
            actionNecessary = true
        } else {
            actionNecessary = currentOrientation!! != newOrientation
        }

        if (actionNecessary) {
            doWriteSettings(context)
        } else {
            // don't do anything if action is not required;
        }
    }

    private fun doWriteSettings(context: Context) {
        if (!AutoCornerHandler.setCorners(context, isEnabled)){
            Toast.makeText(context, context.getString(R.string.msg_check_permission), Toast.LENGTH_SHORT).show()
            context.unregisterReceiver(this)
            isRegistered = false
        }
    }

    companion object {
        private var instance: OrientationChangeListener? = null
        fun getInstance(isEnabled: Boolean, isPersistent: Boolean): OrientationChangeListener {
            if (instance == null) {
                instance = OrientationChangeListener(isEnabled, isPersistent)
            } else {
                instance!!.isEnabled = isEnabled
                instance!!.isPersistent = isPersistent // TODO make this able to change persistency
            }

            return instance!!
        }
    }
}