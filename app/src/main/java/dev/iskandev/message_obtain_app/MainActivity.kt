package dev.iskandev.message_obtain_app

import dev.iskandev.message_obtain_app.viewmodel.MessageViewModel
import dev.iskandev.message_obtain_app.viewmodel.MessageViewModelFactory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    private val POST_SENT_MESSAGE = "POST request has been sent successfully!"
    private val NO_RECORD_MESSAGE = "There are no records yet!"
    private val ERROR_MESSAGE = "Something went wrong...";

    private lateinit var btnPost: Button
    private lateinit var btnGet: Button
    private lateinit var tvMessage: TextView
    private lateinit var tvSHA2: TextView

    private val viewModel by viewModels<MessageViewModel>{
        MessageViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadViewElements()
        setButtonListeners()
        setObservers()
    }

    private fun loadViewElements() {
        btnPost = findViewById(R.id.button_post)
        btnGet = findViewById(R.id.button_get)
        tvMessage = findViewById(R.id.generated_message)
        tvSHA2 = findViewById(R.id.generated_sha2)
    }

    private fun setButtonListeners() {
        btnPost.setOnClickListener {
            viewModel.createMessage(
                { toast(POST_SENT_MESSAGE) },
                { toast(ERROR_MESSAGE) }
            )
        }
        btnGet.setOnClickListener {
            viewModel.getMessage(
                { toast(ERROR_MESSAGE) },
                { toast(NO_RECORD_MESSAGE) }
            )
        }
    }

    private fun setObservers() {
        val messageObserver = Observer<String> { newValue ->
            tvMessage.text = newValue
        }
        val sha2Observer = Observer<String> { newValue ->
            tvSHA2.text = newValue
        }
        viewModel.messageValue.observe(this, messageObserver)
        viewModel.messageSHA2.observe(this, sha2Observer)
    }

    private fun toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, duration).show()
    }
}