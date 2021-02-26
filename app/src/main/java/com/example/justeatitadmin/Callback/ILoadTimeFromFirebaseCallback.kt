package com.example.justeatitadmin.Callback

import com.example.justeatitadmin.Model.OrderModel

interface ILoadTimeFromFirebaseCallback {
    fun onLoadOnlyTimeSuccess(estimatedTimeMs:Long)
    fun onLoadTimeFailed(message:String)
}