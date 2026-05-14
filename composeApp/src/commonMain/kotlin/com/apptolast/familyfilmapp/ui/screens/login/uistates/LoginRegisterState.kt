package com.apptolast.familyfilmapp.ui.screens.login.uistates

import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.login_text_button
import familyfilmkmp.composeapp.generated.resources.login_text_no_account
import familyfilmkmp.composeapp.generated.resources.login_text_sign_in
import familyfilmkmp.composeapp.generated.resources.login_text_sign_up
import familyfilmkmp.composeapp.generated.resources.login_text_yes_account
import familyfilmkmp.composeapp.generated.resources.register_text_button
import org.jetbrains.compose.resources.StringResource

/**
 * Toggle state for the Login/Register form.
 *
 * Legacy version used `@StringRes Int` references into `R.string.*`; the
 * multiplatform equivalent is `StringResource` from Compose Resources,
 * resolved at call site with `stringResource(state.buttonText)`.
 */
sealed class LoginRegisterState(
    val buttonText: StringResource,
    val accountText: StringResource,
    val signText: StringResource,
) {
    data class Login(
        val button: StringResource = Res.string.login_text_button,
        val account: StringResource = Res.string.login_text_no_account,
        val sign: StringResource = Res.string.login_text_sign_up,
    ) : LoginRegisterState(button, account, sign)

    data class Register(
        val button: StringResource = Res.string.register_text_button,
        val account: StringResource = Res.string.login_text_yes_account,
        val sign: StringResource = Res.string.login_text_sign_in,
    ) : LoginRegisterState(button, account, sign)
}
