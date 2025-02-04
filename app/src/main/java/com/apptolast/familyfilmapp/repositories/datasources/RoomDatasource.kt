package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.model.room.GroupTable
import com.apptolast.familyfilmapp.model.room.UserTable
import com.apptolast.familyfilmapp.room.group.GroupDao
import com.apptolast.familyfilmapp.room.user.UserDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomDatasourceImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val userDao: UserDao,
) : RoomDatasource {

    ///////////////////////////////////////////////////////////////////////////
    // Groups
    ///////////////////////////////////////////////////////////////////////////
    override fun getGroups(): Flow<List<GroupTable>> = groupDao.getGroups()


    override fun getGroupById(id: String): Flow<GroupTable> =
        groupDao.getGroup(id)

//    override suspend fun insertGroupWithUsers(groupWithUsers: GroupWithUsers) =
//        groupDao.insertGroupWithUsers(groupWithUsers)

    override suspend fun insertGroup(group: GroupTable) =
        groupDao.insert(group)

    override suspend fun deleteGroup(group: GroupTable) =
        groupDao.delete(group)

    override suspend fun updateGroup(group: GroupTable) =
        groupDao.update(group)

    ///////////////////////////////////////////////////////////////////////////
    // Users
    ///////////////////////////////////////////////////////////////////////////
    override fun getAllUsers(): Flow<List<UserTable>> = userDao.getUsers()
    override fun getUser(id: String): Flow<UserTable?> = userDao.getUser(id)
    override suspend fun insertUser(user: UserTable) = userDao.insert(user)
    override suspend fun deleteUser(user: UserTable) = userDao.delete(user)
    override suspend fun updateUser(user: UserTable) = userDao.update(user)

}


interface RoomDatasource {

    ///////////////////////////////////////////////////////////////////////////
    // Groups
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Retrieve all the groups from the the given data source.
     */
    fun getGroups(): Flow<List<GroupTable>>

    /**
     * Retrieve groups from the given data source that matches with the [id].
     */
    fun getGroupById(id: String): Flow<GroupTable>

    /**
     * Insert group in the data source
     */
//    suspend fun insertGroupWithUsers(groupWithUsers: GroupWithUsers): Boolean
    suspend fun insertGroup(item: GroupTable)

    /**
     * Delete group from the data source
     */
    suspend fun deleteGroup(item: GroupTable)

    /**
     * Update group in the data source
     */
    suspend fun updateGroup(item: GroupTable)


    ///////////////////////////////////////////////////////////////////////////
    // Users
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve all the users from the the given data source.
     */
    fun getAllUsers(): Flow<List<UserTable>>

    /**
     * Retrieve an user from the given data source that matches with the [id].
     */
    fun getUser(id: String): Flow<UserTable?>

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

// https://stackoverflow.com/questions/44667160/android-room-insert-relation-entities-using-room
