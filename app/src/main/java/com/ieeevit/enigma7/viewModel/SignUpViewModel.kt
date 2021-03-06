package com.ieeevit.enigma7.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.ieeevit.enigma7.api.service.Api
import com.ieeevit.enigma7.model.AccessToken
import com.ieeevit.enigma7.work.RefreshXpWorker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class SignUpViewModel : ViewModel() {
    private val _authStatus = MutableLiveData<Int>()
    val authStatus: LiveData<Int>
        get() = _authStatus
    private val _authCode = MutableLiveData<String>()
    val authCode: LiveData<String>
        get() = _authCode
    private val _userStatus = MutableLiveData<Boolean>()
    val userStatus: LiveData<Boolean>
        get() = _userStatus
    private val clientId: String =
        "55484635453-c46tes445anbidhb2qnmb2qs618mvpni.apps.googleusercontent.com"

    init {
        _authStatus.value = 3   // 0:fail 1:success
        _authCode.value = null
        _userStatus.value = null
    }

    val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(Scope(Scopes.EMAIL))
        .requestServerAuthCode(clientId)
        .requestEmail()
        .build()

    fun startXpRetrieval(authToken: String) {
        GlobalScope.launch {
            setRecurringWork(authToken)
        }
    }

    private fun setRecurringWork(authToken: String) {
        val data = Data.Builder()
        data.putString("auth_token", authToken)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshXpWorker>(
            1,
            TimeUnit.HOURS
        ).setInputData(data.build()).setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshXpWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    fun getAuthCode(code: String, redirectUri: String) {
        Api.retrofitService.getAccessToken(code, redirectUri)
            .enqueue(object : Callback<AccessToken> {
                override fun onResponse(
                    call: Call<AccessToken>,
                    response: Response<AccessToken>
                ) {
                    if (response.body() != null) {
                        Log.i("AUthKEY", response.body().toString())
                        val result: AccessToken? = response.body()
                        _authCode.value = result?.authorizationKey
                        _userStatus.value = result?.usernameExist
                        _authStatus.value = 1
                    }
                }

                override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                    _authStatus.value = 0
                }
            })
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SignUpViewModel() as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}