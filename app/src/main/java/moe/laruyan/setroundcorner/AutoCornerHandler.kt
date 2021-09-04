package moe.laruyan.setroundcorner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.io.IOException
import java.lang.RuntimeException

class AutoCornerHandler {
    companion object {
        private val LOG_TAG = "AutoCornerHandler"

        /**
         * run commands to set corners.
         * @param context android.content.Context
         * @param isEnabled should run commands
         * @return returns true if errors not found, if found, returns false.
         */
        fun setCorners(context: Context, isEnabled: Boolean): Boolean {
            val roundedSize: Int = 57 // in dip (maybe). currently 57 is for A30 in HD+
            val contentPadding: Int =
                0 // in dip (maybe). currently the developer couldn't found the right value for it. due to Android 11 removed this from secure setting.

            var gotError = false

            if (context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                gotError = true
            }

            if (!gotError && isEnabled) {
                try {
                    // first, reset to refresh
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    Runtime.getRuntime().exec("settings put secure sysui_rounded_size 0");// not works
                        Settings.Secure.putInt(context.contentResolver, "sysui_rounded_size", 0);
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        // sysui_rounded_content_padding removed with Android 11
//                    Runtime.getRuntime().exec("settings put secure sysui_rounded_content_padding 0");// not works
                        Settings.Secure.putInt(
                            context.contentResolver,
                            "sysui_rounded_content_padding",
                            0
                        );
                    }
                } catch (ioe: IOException) {
                    Log.e(LOG_TAG, "IOException caught!", ioe)
                    gotError = true
                } catch (re: RuntimeException) {
                    Log.e(LOG_TAG, "RuntimeExeption caught!", re)
                    gotError = true
                } catch (se: SecurityException) {
                    Log.e(LOG_TAG, "SecurityException caught!", se)
                    gotError = true
                }

                try {
                    // second, apply wanted values.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    Runtime.getRuntime().exec("settings put secure sysui_rounded_size ${roundedSize}"); // not works
                        Settings.Secure.putInt(
                            context.contentResolver,
                            "sysui_rounded_size",
                            roundedSize
                        );
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        // sysui_rounded_content_padding removed with Android 11
//                    Runtime.getRuntime().exec("settings put secure sysui_rounded_content_padding ${contentPadding}"); // not works
                        Settings.Secure.putInt(
                            context.contentResolver,
                            "sysui_rounded_content_padding",
                            contentPadding
                        );
                    }
                } catch (ioe: IOException) {
                    Log.e(LOG_TAG, "IOException caught!", ioe)
                    gotError = true
                } catch (re: RuntimeException) {
                    Log.e(LOG_TAG, "RuntimeExeption caught!", re)
                    gotError = true
                } catch (se: SecurityException) {
                    Log.e(LOG_TAG, "SecurityException caught!", se)
                    gotError = true
                }
            }

            // finally, return results
            return !gotError
        }
    }
}