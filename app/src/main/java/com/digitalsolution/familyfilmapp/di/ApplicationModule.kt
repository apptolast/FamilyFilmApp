package com.digitalsolution.familyfilmapp.di

import com.digitalsolution.familyfilmapp.repositories.FakeRepository
import com.digitalsolution.familyfilmapp.repositories.FakeRepositoryImpl
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
object ApplicationModule {

    @Singleton
    @Provides
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): LoginRepository =
        LoginRepositoryImpl(firebaseAuth)


    @Singleton
    @Provides
    fun provideFakeRepository(): FakeRepository = FakeRepositoryImpl()
}
