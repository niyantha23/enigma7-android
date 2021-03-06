package com.ieeevit.enigma7.model

import com.google.gson.annotations.SerializedName
import java.lang.Boolean.FALSE

class AccessToken {
    @SerializedName("key")
    val authorizationKey: String = ""

    @SerializedName("username_exists")
    val usernameExist: Boolean = FALSE

}
