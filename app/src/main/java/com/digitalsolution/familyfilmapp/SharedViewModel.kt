package com.digitalsolution.familyfilmapp

import androidx.lifecycle.ViewModel
import com.digitalsolution.familyfilmapp.model.local.MemeberData
import com.digitalsolution.familyfilmapp.repositories.FakeRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val fakeRepositoryImpl: FakeRepositoryImpl
) : ViewModel() {

    fun getGroups() = fakeRepositoryImpl.generateGroups(12)

    fun getMembers() = fakeRepositoryImpl.generateMembersForGroups(12)

    fun removeElementMember(memeberData: MemeberData, memebers: List<MemeberData>) {
        val list = memebers.toMutableList()
        for (i in 0 until list.size) {
            if (list[i].name.equals(memeberData.name, true)) {
                list.remove(list[i])
            }
        }
    }
}
