package com.apptolast.familyfilmapp.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.apptolast.familyfilmapp.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
    fun provideGoogleSignInOption(): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.WEB_ID_CLIENT) // Replace with your Web Client ID
            .requestEmail()
            .build()


    @Singleton
    @Provides
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)

    @Singleton
    @Provides
    fun provideCredentialRequest(
        signInWithGoogleOption: GetSignInWithGoogleOption,
    ): GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

    @Singleton
    @Provides
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        googleSignInOptions: GoogleSignInOptions,
    ): GoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
}
