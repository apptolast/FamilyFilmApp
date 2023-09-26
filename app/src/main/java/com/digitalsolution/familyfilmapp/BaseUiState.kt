package com.digitalsolution.familyfilmapp

import com.digitalsolution.familyfilmapp.exceptions.CustomException

interface BaseUiState {
     val isLoading: Boolean
     val errorMessage: CustomException?
}
