package com.apptolast.familyfilmapp.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.apptolast.familyfilmapp.BuildConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleSignInModule {

    @Singleton
    @Provides
    fun signInWithGoogleOption(): GetSignInWithGoogleOption =
        GetSignInWithGoogleOption.Builder(BuildConfig.WEB_ID_CLIENT)
            .build()

    @Singleton
    @Provides
    fun credentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)

    @Singleton
    @Provides
    fun credentialRequest(
        signInWithGoogleOption: GetSignInWithGoogleOption
    ): GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()
}
