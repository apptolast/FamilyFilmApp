package com.apptolast.familyfilmapp.di

import android.content.Context
import com.apptolast.familyfilmapp.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
    fun provideGoogleSignOptions(): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.WEB_ID_CLIENT)
            .requestId()
            .requestProfile()
            .build()

    @Singleton
    @Provides
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        googleSignInOptions: GoogleSignInOptions,
    ): GoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
}
