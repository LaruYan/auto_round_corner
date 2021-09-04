package moe.laruyan.setroundcorner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private var tvGuideCommand: TextView? = null
    private var tvGuideCommandActual: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvGuideCommand = findViewById(R.id.text_guide_command)
        tvGuideCommandActual = findViewById(R.id.text_guide_command_actual)
    }

    override fun onResume() {
        super.onResume()
        checkForPermission()
    }

    private fun checkForPermission() {
        if (AutoCornerHandler.setCorners(applicationContext, true)) {
            tvGuideCommand?.text = getString(R.string.msg_service_started)
            tvGuideCommandActual?.text = ""
            AutoCornerService.startActionEnter(applicationContext, true, true)
        } else {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.msg_check_permission), Toast.LENGTH_SHORT).show()
            tvGuideCommand?.text = getString(R.string.msg_guide_command)
            tvGuideCommandActual?.text = getString(R.string.msg_guide_command_actual)
            AutoCornerService.startActionExit(applicationContext)
        }
    }
}