package com.example.justeatitadmin.Common

import com.example.justeatitadmin.Model.CategoryModel
import com.example.justeatitadmin.Model.FoodModel
import com.example.justeatitadmin.Model.ServerUserModel

object Common {
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