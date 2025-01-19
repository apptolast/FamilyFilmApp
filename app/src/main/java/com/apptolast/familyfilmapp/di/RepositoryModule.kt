package com.apptolast.familyfilmapp.di

import com.apptolast.familyfilmapp.network.BackendApi
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.repositories.BackendRepositoryImpl
import com.apptolast.familyfilmapp.repositories.FirebaseRepository
import com.apptolast.familyfilmapp.repositories.FirebaseRepositoryImpl
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
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): FirebaseRepository = FirebaseRepositoryImpl(firebaseAuth)

//    @Singleton
//    @Provides
//    fun provideLocalRepository(prefs: SharedPreferenceManager): LocalRepository = LocalRepositoryImpl(prefs)

    @Singleton
    @Provides
    fun provideBackendRepository(backendApi: BackendApi): BackendRepository = BackendRepositoryImpl(backendApi)
}
