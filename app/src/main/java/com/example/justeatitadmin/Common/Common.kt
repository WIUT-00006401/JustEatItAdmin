package com.example.justeatitadmin.Common

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.TextView
import com.example.justeatitadmin.Model.CategoryModel
import com.example.justeatitadmin.Model.FoodModel
import com.example.justeatitadmin.Model.ServerUserModel

object Common {

    fun setSpanString(welcome: String, name: String?, txtUser: TextView?) {
        val builder = SpannableStringBuilder()
        builder.append(welcome)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan, 0, name!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        txtUser!!.setText(builder, TextView.BufferType.SPANNABLE)
    }

    val SERVER_REF = "Server"
    var currentServerUser: ServerUserModel?=null

    const val CATEGORY_REF: String="Category"
    var foodSelected: FoodModel?=null
    var categorySelected: CategoryModel?=null
    val DEFAULT_COLUMN_COUNT: Int=0
    val FULL_WIDTH_COLUMN: Int = 1
    const val NOTI_TITLE = "title"
    const val NOTI_CONTENT = "content"
    const val TOKEN_REF = "Tokens"
}