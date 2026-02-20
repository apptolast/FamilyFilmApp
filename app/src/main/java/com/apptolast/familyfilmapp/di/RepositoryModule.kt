package com.apptolast.familyfilmapp.di

import androidx.work.WorkManager
import com.apptolast.familyfilmapp.network.TmdbApi
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
import com.apptolast.familyfilmapp.room.groupmoviestatus.GroupMovieStatusDao
import com.apptolast.familyfilmapp.room.user.UserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): FirebaseAuthRepository =
        FirebaseAuthRepositoryImpl(firebaseAuth)

    @Singleton
    @Provides
    fun provideRepository(
        roomDatasource: RoomDatasource,
        firebaseDatabaseDatasource: FirebaseDatabaseDatasource,
        tmdbDatasource: TmdbDatasource,
        workManager: WorkManager,
        coroutineScope: CoroutineScope,
    ): Repository = RepositoryImpl(
        roomDatasource,
        firebaseDatabaseDatasource,
        tmdbDatasource,
        workManager,
        coroutineScope,
    )

    @Singleton
    @Provides
    fun provideRoomDatasource(
        groupDao: GroupDao,
        userDao: UserDao,
        groupMovieStatusDao: GroupMovieStatusDao,
    ): RoomDatasource = RoomDatasourceImpl(groupDao, userDao, groupMovieStatusDao)

    @Singleton
    @Provides
    fun provideFirebaseDatabaseDatasource(database: FirebaseFirestore): FirebaseDatabaseDatasource =
        FirebaseDatabaseDatasourceImpl(database)

    @Singleton
    @Provides
    fun provideTmdbDatasource(tmdbApi: TmdbApi): TmdbDatasource = TmdbDatasourceImpl(tmdbApi)
}
