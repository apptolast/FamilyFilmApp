package com.digitalsolution.familyfilmapp.di

import com.digitalsolution.familyfilmapp.managers.SharedPreferenceManager
import com.digitalsolution.familyfilmapp.network.BackendApi
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import com.digitalsolution.familyfilmapp.repositories.BackendRepositoryImpl
import com.digitalsolution.familyfilmapp.repositories.LocalRepository
import com.digitalsolution.familyfilmapp.repositories.LocalRepositoryImpl
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.repositories.LoginRepositoryImpl
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
