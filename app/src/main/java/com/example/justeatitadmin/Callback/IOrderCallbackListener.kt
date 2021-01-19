package com.example.justeatitadmin.Callback

import com.example.justeatitadmin.Model.OrderModel

interface IOrderCallbackListener {
    fun onOrderLoadSuccess(orderModel: List<OrderModel>)
    fun onOrderLoadFailed(message:String)
}