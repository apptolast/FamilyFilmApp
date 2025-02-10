package com.apptolast.familyfilmapp.di

import com.apptolast.familyfilmapp.network.BackendApi
import com.apptolast.familyfilmapp.network.TmdbApi
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.repositories.BackendRepositoryImpl
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepositoryImpl
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.repositories.RepositoryImpl
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasource
import com.apptolast.familyfilmapp.repositories.datasources.FirebaseDatabaseDatasourceImpl
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasourceImpl
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasourceImpl
import com.apptolast.familyfilmapp.room.group.GroupDao
import com.apptolast.familyfilmapp.room.user.UserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): FirebaseAuthRepository =
        FirebaseAuthRepositoryImpl(firebaseAuth)

//    @Singleton
//    @Provides
//    fun provideLocalRepository(prefs: SharedPreferenceManager): LocalRepository = LocalRepositoryImpl(prefs)

    @Singleton
    @Provides
    fun provideBackendRepository(backendApi: BackendApi): BackendRepository = BackendRepositoryImpl(backendApi)

    @Provides
    fun provideRepository(
        firebaseAuth: FirebaseAuth,
        roomDatasource: RoomDatasource,
        firebaseDatabaseDatasource: FirebaseDatabaseDatasource,
        tmdbDatasource: TmdbDatasource,
    ): Repository = RepositoryImpl(firebaseAuth, roomDatasource, firebaseDatabaseDatasource, tmdbDatasource)

    @Provides
    fun provideRoomDatasource(groupDao: GroupDao, userDao: UserDao): RoomDatasource =
        RoomDatasourceImpl(groupDao, userDao)

    @Provides
    fun provideFirebaseDatabaseDatasource(
        database: FirebaseFirestore,
        roomDatasource: RoomDatasource,
    ): FirebaseDatabaseDatasource = FirebaseDatabaseDatasourceImpl(database, roomDatasource)

    @Provides
    fun provideTmdbDatasource(tmdbApi: TmdbApi): TmdbDatasource = TmdbDatasourceImpl(tmdbApi)
}
