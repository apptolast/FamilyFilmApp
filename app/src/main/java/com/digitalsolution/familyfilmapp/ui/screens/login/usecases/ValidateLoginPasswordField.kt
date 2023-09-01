package com.digitalsolution.familyfilmapp.ui.screens.login.usecases


fun String.validatePasswordLoginField(): Boolean {
    return (this.isNotEmpty() && this.contains("^(?=.*[0-9])(?=.*[\\W_]).+\$") && this.length >= 6)
}