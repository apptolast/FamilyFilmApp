package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.model.local.MediaKey
import com.apptolast.familyfilmapp.model.room.GroupMovieStatusTable
import com.apptolast.familyfilmapp.model.room.GroupTable
import com.apptolast.familyfilmapp.model.room.SkippedMediaTable
import com.apptolast.familyfilmapp.model.room.UserTable
import com.apptolast.familyfilmapp.room.group.GroupDao
import com.apptolast.familyfilmapp.room.groupmoviestatus.GroupMovieStatusDao
import com.apptolast.familyfilmapp.room.skippedmedia.SkippedMediaDao
import com.apptolast.familyfilmapp.room.user.UserDao
import kotlinx.coroutines.flow.Flow

class RoomDatasourceImpl(
    private val groupDao: GroupDao,
    private val userDao: UserDao,
    private val groupMovieStatusDao: GroupMovieStatusDao,
    private val skippedMediaDao: SkippedMediaDao,
) : RoomDatasource {

    // Groups
    override fun getGroups(): Flow<List<GroupTable>> = groupDao.getGroups()
    override fun getMyGroups(userId: String): Flow<List<GroupTable>> = groupDao.getMyGroups(userId)
    override fun getGroupById(id: String): Flow<GroupTable?> = groupDao.getGroup(id)
    override suspend fun insertGroup(group: GroupTable) = groupDao.insert(group)
    override suspend fun updateGroup(group: GroupTable) = groupDao.update(group)
    override suspend fun deleteGroup(group: GroupTable) = groupDao.delete(group)

    // Users
    override fun getAllUsers(): Flow<List<UserTable>> = userDao.getUsers()
    override fun getUser(id: String): Flow<UserTable?> = userDao.getUser(id)
    override fun getUserByEmail(email: String): Flow<UserTable?> = userDao.getUserByEmail(email)
    override suspend fun getUserByUsername(username: String): UserTable? = userDao.getUserByUsername(username)
    override suspend fun getUsersByIds(userIds: List<String>): List<UserTable> = userDao.getUsersByIds(userIds)
    override suspend fun insertUser(user: UserTable) = userDao.insert(user)
    override suspend fun deleteUser(user: UserTable) = userDao.delete(user)
    override suspend fun updateUser(user: UserTable) = userDao.update(user)

    // Movie Statuses (per-group)
    override suspend fun insertMovieStatus(entry: GroupMovieStatusTable) = groupMovieStatusDao.insert(entry)
    override suspend fun insertAllMovieStatuses(entries: List<GroupMovieStatusTable>) =
        groupMovieStatusDao.insertAll(entries)

    override suspend fun deleteMovieStatus(groupId: String, userId: String, movieId: Int, mediaType: String) =
        groupMovieStatusDao.delete(groupId, userId, movieId, mediaType)

    override fun getMovieStatusesByGroup(groupId: String): Flow<List<GroupMovieStatusTable>> =
        groupMovieStatusDao.getStatusesByGroup(groupId)

    override fun getMovieStatusesByGroupAndUser(groupId: String, userId: String): Flow<List<GroupMovieStatusTable>> =
        groupMovieStatusDao.getStatusesByGroupAndUser(groupId, userId)

    override fun getMovieStatusesByUser(userId: String): Flow<List<GroupMovieStatusTable>> =
        groupMovieStatusDao.getStatusesByUser(userId)

    override suspend fun getAllMarkedMediaKeysForUser(userId: String): List<MediaKey> =
        groupMovieStatusDao.getAllMediaKeysForUser(userId)

    override suspend fun deleteMovieStatusesByGroup(groupId: String) = groupMovieStatusDao.deleteByGroup(groupId)
    override suspend fun deleteAllMovieStatuses() = groupMovieStatusDao.deleteAll()

    // Skipped Media
    override suspend fun insertSkippedMedia(entry: SkippedMediaTable) = skippedMediaDao.insert(entry)
    override fun observeSkippedMedia(userId: String): Flow<List<SkippedMediaTable>> =
        skippedMediaDao.observeByUser(userId)

    override suspend fun getSkippedMediaKeysForUser(userId: String): List<MediaKey> =
        skippedMediaDao.getKeysByUser(userId)

    override suspend fun getSkippedMedia(userId: String, mediaKey: MediaKey): SkippedMediaTable? =
        skippedMediaDao.getByKey(userId, mediaKey.mediaId, mediaKey.mediaType.name)

    override suspend fun deleteSkippedMedia(userId: String, mediaKey: MediaKey) =
        skippedMediaDao.deleteByKey(userId, mediaKey.mediaId, mediaKey.mediaType.name)

    override suspend fun deleteAllSkippedMedia() = skippedMediaDao.deleteAll()

    override suspend fun clearAllData() {
        skippedMediaDao.deleteAll()
        groupMovieStatusDao.deleteAll()
        groupDao.deleteAll()
        userDao.deleteAll()
    }
}

interface RoomDatasource {
    // Groups
    fun getGroups(): Flow<List<GroupTable>>
    fun getMyGroups(userId: String): Flow<List<GroupTable>>
    fun getGroupById(id: String): Flow<GroupTable?>
    suspend fun insertGroup(group: GroupTable)
    suspend fun deleteGroup(group: GroupTable)
    suspend fun updateGroup(group: GroupTable)

    // Users
    fun getAllUsers(): Flow<List<UserTable>>
    fun getUser(id: String): Flow<UserTable?>
    fun getUserByEmail(email: String): Flow<UserTable?>
    suspend fun getUserByUsername(username: String): UserTable?
    suspend fun getUsersByIds(userIds: List<String>): List<UserTable>
    suspend fun insertUser(user: UserTable)
    suspend fun deleteUser(user: UserTable)
    suspend fun updateUser(user: UserTable)

    /** Clear all local data on logout to prevent leaking between sessions. */
    suspend fun clearAllData()

    // Movie Statuses (per-group)
    suspend fun insertMovieStatus(entry: GroupMovieStatusTable)
    suspend fun insertAllMovieStatuses(entries: List<GroupMovieStatusTable>)
    suspend fun deleteMovieStatus(groupId: String, userId: String, movieId: Int, mediaType: String = "MOVIE")
    fun getMovieStatusesByGroup(groupId: String): Flow<List<GroupMovieStatusTable>>
    fun getMovieStatusesByGroupAndUser(groupId: String, userId: String): Flow<List<GroupMovieStatusTable>>
    fun getMovieStatusesByUser(userId: String): Flow<List<GroupMovieStatusTable>>
    suspend fun getAllMarkedMediaKeysForUser(userId: String): List<MediaKey>
    suspend fun deleteMovieStatusesByGroup(groupId: String)
    suspend fun deleteAllMovieStatuses()

    // Skipped Media
    suspend fun insertSkippedMedia(entry: SkippedMediaTable)
    fun observeSkippedMedia(userId: String): Flow<List<SkippedMediaTable>>
    suspend fun getSkippedMediaKeysForUser(userId: String): List<MediaKey>
    suspend fun getSkippedMedia(userId: String, mediaKey: MediaKey): SkippedMediaTable?
    suspend fun deleteSkippedMedia(userId: String, mediaKey: MediaKey)
    suspend fun deleteAllSkippedMedia()
}
