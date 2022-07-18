package com.team.alex.talent.presentation.utils

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText()

    class DynamicString(
        val value: String
    ) : UiText()

    fun toString(context: Context): String {
        return when(this){
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}