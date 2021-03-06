package com.ieeevit.enigma7.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.ieeevit.enigma7.api.service.Api
import com.ieeevit.enigma7.database.getDatabase
import com.ieeevit.enigma7.model.LogoutResponse
import com.ieeevit.enigma7.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(getDatabase(application))
    private val _logoutStatus = MutableLiveData<String>()
    val logoutStatus: LiveData<String>
        get() = _logoutStatus
    private val clientId: String =
        "55484635453-c46tes445anbidhb2qnmb2qs618mvpni.apps.googleusercontent.com"
    val networkStatus = MutableLiveData<Int>()

    init {
        _logoutStatus.value = null
    }

    val userDetails = repository.userDetails
    val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
        .requestServerAuthCode(clientId)
        .requestEmail()
        .build()

    fun refreshUserDetailsFromRepository(authToken: String) {
        viewModelScope.launch {
            try {
                repository.refreshUserDetails(authToken)
            } catch (e: Exception) {
                networkStatus.value = 0

            }
        }
    }

    fun clearCacheOnLogOut() {
        viewModelScope.launch {
            try {
                repository.clearCache()
            } catch (e: Exception) {

            }
        }
    }

    fun logOut(authToken: String) {
        Api.retrofitService.logOut(authToken).enqueue((object : Callback<LogoutResponse> {
            override fun onResponse(
                call: Call<LogoutResponse>,
                response: Response<LogoutResponse>
            ) {
                if (response.body() != null) {
                    val body: LogoutResponse? = response.body()
                    _logoutStatus.value = body?.detail
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                networkStatus.value = 0
            }
        }))
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}