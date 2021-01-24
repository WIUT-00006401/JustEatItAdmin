package com.example.justeatitadmin.Callback

import com.example.justeatitadmin.Model.ShippingOrderModel

interface ISingleShippingOrderCallbackListener {
    fun onSingleShippingOrderSuccess(shippingOrderModel: ShippingOrderModel)
}