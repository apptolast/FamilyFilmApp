package com.digitalsolution.familyfilmapp.exceptions


sealed class LoginAndRegisterExceptions(val message: String) {
    object EmailBlank : LoginAndRegisterExceptions("Email is Blank")
    object PassBlank : LoginAndRegisterExceptions("Password is Blank")
    object EmailInvalidFormat :
        LoginAndRegisterExceptions("Email its not valid the format will be that : example@gmail.com")
    object PasswordInavalidFormat :
        LoginAndRegisterExceptions("Password its ni valid should be contain : NotBlank, Character Especial numbers and UpperCaseLetters, and must be greater or equal than 6")
}