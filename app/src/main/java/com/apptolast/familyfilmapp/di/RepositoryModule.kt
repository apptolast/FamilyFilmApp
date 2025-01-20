package com.apptolast.familyfilmapp.di

import com.apptolast.familyfilmapp.network.BackendApi
import com.apptolast.familyfilmapp.network.TmdbApi
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.repositories.BackendRepositoryImpl
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepositoryImpl
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.repositories.RepositoryImpl
import com.apptolast.familyfilmapp.repositories.TmdbDatasource
import com.apptolast.familyfilmapp.repositories.TmdbDatasourceImpl
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
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): FirebaseAuthRepository = FirebaseAuthRepositoryImpl(firebaseAuth)

//    @Singleton
//    @Provides
//    fun provideLocalRepository(prefs: SharedPreferenceManager): LocalRepository = LocalRepositoryImpl(prefs)

    @Singleton
    @Provides
    fun provideBackendRepository(backendApi: BackendApi): BackendRepository = BackendRepositoryImpl(backendApi)


    @Provides
    fun provideRepository(tmdbDatasource: TmdbDatasource): Repository =
        RepositoryImpl(tmdbDatasource)

    @Provides
    fun provideTmdbDatasource(tmdbApi: TmdbApi): TmdbDatasource =
        TmdbDatasourceImpl(tmdbApi)
}
