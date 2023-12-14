package com.apptolast.familyfilmapp.ui.screens

import com.apptolast.familyfilmapp.repositories.BackendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    private val repository: BackendRepository
) {



}
