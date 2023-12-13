package com.apptolast.familyfilmapp.di

import com.apptolast.familyfilmapp.managers.SharedPreferenceManager
import com.apptolast.familyfilmapp.network.BackendApi
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.repositories.BackendRepositoryImpl
import com.apptolast.familyfilmapp.repositories.LocalRepository
import com.apptolast.familyfilmapp.repositories.LocalRepositoryImpl
import com.apptolast.familyfilmapp.repositories.LoginRepository
import com.apptolast.familyfilmapp.repositories.LoginRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): LoginRepository = LoginRepositoryImpl(firebaseAuth)

    @Singleton
    @Provides
    fun provideLocalRepository(prefs: SharedPreferenceManager): LocalRepository = LocalRepositoryImpl(prefs)

    @Singleton
    @Provides
    fun provideBackendRepository(backendApi: BackendApi, localRepository: LocalRepository): BackendRepository =
        BackendRepositoryImpl(backendApi, localRepository)
}
