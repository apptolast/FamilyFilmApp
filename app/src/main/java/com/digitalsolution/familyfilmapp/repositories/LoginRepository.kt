package com.digitalsolution.familyfilmapp.repositories

import javax.inject.Inject

class LoginRepository @Inject constructor() : LoginRepositoryInterface {

    override fun login(username: String, password: String) {
        // TODO: Implement this method
    }

}

interface LoginRepositoryInterface {
    fun login(username: String, password: String)
}
