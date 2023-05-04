/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ProfileViewModel.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.profile.user

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.my.kizzy.domain.model.User
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.USER_DATA
import com.my.kizzy.preference.Prefs.USER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserUseCase: com.my.kizzy.domain.use_case.get_user.GetUserUseCase
): ViewModel() {

    private val _state = mutableStateOf(UserState())
    val state: State<UserState> = _state

    init {
        getUser()
    }
     private fun getUser(){
        getUserUseCase(Prefs[USER_ID,""]).onEach { result ->
            when(result){
                is com.my.kizzy.domain.model.Resource.Success -> {
                    _state.value = UserState(user = result.data)
                    Prefs[USER_DATA] = Gson().toJson(result.data)
                }
                is com.my.kizzy.domain.model.Resource.Error -> {
                    val user = Gson().fromJson(Prefs[USER_DATA,"{}"], User::class.java)
                    _state.value = UserState(
                        error = result.message?: "An unexpected error occurred",
                        user = user
                    )
                }
                is com.my.kizzy.domain.model.Resource.Loading -> {
                    _state.value = UserState(loading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}
