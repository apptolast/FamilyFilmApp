package com.digitalsolution.familyfilmapp.repositories

import com.digitalsolution.familyfilmapp.managers.SharedPreferencesManager
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferencesManager
) : LocalRepository {

    override fun setToken(token: String?) = prefs.setToken(token)

    override fun getToken(): String? = prefs.getToken()

}

interface LocalRepository {
    fun setToken(token: String?)
    fun getToken(): String?
}
