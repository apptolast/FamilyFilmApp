package com.digitalsolution.familyfilmapp

import androidx.lifecycle.ViewModel
import com.digitalsolution.familyfilmapp.repositories.FakeRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val fakeRepositoryImpl: FakeRepositoryImpl
) : ViewModel() {

    fun getGroups() = fakeRepositoryImpl.generateGroups(12)

    fun getMembers() = fakeRepositoryImpl.generateMembersForGroups(12)
}