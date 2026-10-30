package com.protocol.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.protocol.databinding.ActivityLogsBinding
import com.protocol.log.EventLog
import java.io.File

/** Просмотр и передача файлов лога. SPEC 6.4. */
class LogsActivity : AppCompatActivity() {
    private lateinit var b: ActivityLogsBinding
    private var files: List<File> = emptyList()
    private var current: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(b.root)

        files = EventLog(this).listFiles()
        b.fileSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, files.map { it.name })
        b.fileSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                current = files[position]
                b.logText.text = runCatching { current!!.readText() }.getOrElse { "Не читается: ${it.message}" }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        b.shareButton.setOnClickListener {
            val f = current ?: return@setOnClickListener
            val uri = FileProvider.getUriForFile(this, "com.protocol.files", f)
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, f.name))
        }
    }
}
