package com.digitalsolution.familyfilmapp.managers

import android.content.Context
import javax.inject.Inject

class ResourceProvider @Inject constructor(private val context: Context) {
    fun getString(resId: Int, vararg formatArgs: Any) = context.getString(resId, *formatArgs)
}
