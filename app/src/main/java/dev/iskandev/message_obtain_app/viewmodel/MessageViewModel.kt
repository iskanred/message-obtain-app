package dev.iskandev.message_obtain_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import dev.iskandev.message_obtain_app.repository.MessageRepository
import dev.iskandev.message_obtain_app.repository.Result
import dev.iskandev.message_obtain_app.exception.MessageNotFoundException

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.iskandev.message_obtain_app.model.Message
import dev.iskandev.message_obtain_app.repository.MessageRepositoryImpl
import kotlinx.coroutines.launch


class MessageViewModelFactory() : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MessageViewModel(MessageRepositoryImpl()) as T
    }
}

class MessageViewModel(private val repository: MessageRepository) : ViewModel() {

    val messageValue by lazy {
        MutableLiveData<String>()
    }

    val messageSHA2 by lazy {
        MutableLiveData<String>()
    }

    fun createMessage(successCallback: () -> Unit, errorCallback: () -> Unit) = viewModelScope.launch {
        when (repository.createMessage()) {
            is Result.Success<*> ->  successCallback()
            else -> errorCallback()
        }
    }

    fun getMessage(errorCallback: () -> Unit, notFoundCallback: () -> Unit) = viewModelScope.launch {
        val result = repository.getMessage()
        with(result) {
            when {
                this is Result.Success<*> -> {
                    messageValue.value = (data as Message).message
                    messageSHA2.value = data.sha256
                    Log.i(MessageViewModel::class.qualifiedName, "Got message: ${messageValue}\tsha2: {$messageSHA2}")
                }
                this is Result.Error && exception is MessageNotFoundException -> notFoundCallback()
                else -> errorCallback()
            }
        }
    }
}