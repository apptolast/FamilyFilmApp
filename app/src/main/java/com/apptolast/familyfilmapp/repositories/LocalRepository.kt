package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.managers.SharedPreferenceManager
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferenceManager,
) : LocalRepository {

    override fun setToken(token: String?) = prefs.setToken(token)

    override fun getToken(): String? = prefs.getToken()

    override fun getUserId(): Int = prefs.getUserId()
}

interface LocalRepository {
    fun setToken(token: String?)
    fun getToken(): String?
    fun getUserId(): Int
}
