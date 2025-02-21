package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.model.room.GroupTable
import com.apptolast.familyfilmapp.model.room.UserTable
import com.apptolast.familyfilmapp.room.group.GroupDao
import com.apptolast.familyfilmapp.room.user.UserDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class RoomDatasourceImpl @Inject constructor(private val groupDao: GroupDao, private val userDao: UserDao) :
    RoomDatasource {

    // /////////////////////////////////////////////////////////////////////////
    // Groups
    // /////////////////////////////////////////////////////////////////////////
    override fun getGroups(): Flow<List<GroupTable>> = groupDao.getGroups()
    override fun getMyGroups(userId: String): Flow<List<GroupTable>> = groupDao.getMyGroups(userId)
    override fun getGroupById(id: String): Flow<GroupTable> = groupDao.getGroup(id)
    override suspend fun insertGroup(group: GroupTable) = groupDao.insert(group)
    override suspend fun updateGroup(group: GroupTable) = groupDao.update(group)
    override suspend fun deleteGroup(group: GroupTable) = groupDao.delete(group)

    // /////////////////////////////////////////////////////////////////////////
    // Users
    // /////////////////////////////////////////////////////////////////////////
    override fun getAllUsers(): Flow<List<UserTable>> = userDao.getUsers()
    override fun getUser(id: String): Flow<UserTable?> = userDao.getUser(id)
    override fun getUserByEmail(email: String): Flow<UserTable> = userDao.getUserByEmail(email)
    override suspend fun insertUser(user: UserTable) = userDao.insert(user)
    override suspend fun deleteUser(user: UserTable) = userDao.delete(user)
    override suspend fun updateUser(user: UserTable) = userDao.update(user)

    companion object {
        const val MINIMUM_UPDATE_TIME = 1 * 60 * 60 * 1000 // 1 hour
    }
}

interface RoomDatasource {

    // /////////////////////////////////////////////////////////////////////////
    // Groups
    // /////////////////////////////////////////////////////////////////////////
    /**
     * Retrieve all the groups from the given data source.
     */
    fun getGroups(): Flow<List<GroupTable>>

    /**
     * Retrieve all the groups from the given userId.
     */
    fun getMyGroups(userId: String): Flow<List<GroupTable>>

    /**
     * Retrieve groups from the given data source that matches with the [id].
     */
    fun getGroupById(id: String): Flow<GroupTable>

    /**
     * Insert group in the data source
     */
    suspend fun insertGroup(group: GroupTable)

    /**
     * Delete group from the data source
     */
    suspend fun deleteGroup(group: GroupTable)

    /**
     * Update group in the data source
     */
    suspend fun updateGroup(group: GroupTable)

    // /////////////////////////////////////////////////////////////////////////
    // Users
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve all the users from the the given data source.
     */
    fun getAllUsers(): Flow<List<UserTable>>

    /**
     * Retrieve an user from the given data source that matches with the [id].
     */
    fun getUser(id: String): Flow<UserTable?>

    /**
     * Retrieve an user from the given data source that matches with the [id].
     */
    fun getUserByEmail(email: String): Flow<UserTable?>

    /**
     * Insert user in the data source
     */
    suspend fun insertUser(user: UserTable)

    /**
     * Delete user from the data source
     */
    suspend fun deleteUser(user: UserTable)

    /**
     * Update user in the data source
     */
    suspend fun updateUser(user: UserTable)
}
