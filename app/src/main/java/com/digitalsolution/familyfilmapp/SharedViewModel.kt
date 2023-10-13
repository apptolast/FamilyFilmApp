package com.digitalsolution.familyfilmapp

import androidx.lifecycle.ViewModel
import com.digitalsolution.familyfilmapp.repositories.FilmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val filmRepository: FilmRepository
) : ViewModel() {


    fun getMembers() = filmRepository.generateMembersForGroups(12)

//    fun removeElementMember(memeberData: MemeberData, memebers: List<MemeberData>) {
//        val list = memebers.toMutableList()
//        for (i in 0 until list.size) {
//            if (list[i].name.equals(memeberData.name, true)) {
//                list.remove(list[i])
//            }
//        }
//    }
}