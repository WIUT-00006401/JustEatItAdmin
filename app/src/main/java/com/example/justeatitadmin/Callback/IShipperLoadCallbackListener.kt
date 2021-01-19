package com.example.justeatitadmin.Callback

import android.app.AlertDialog
import android.widget.Button
import android.widget.RadioButton
import com.example.justeatitadmin.Model.OrderModel
import com.example.justeatitadmin.Model.ShipperModel

interface IShipperLoadCallbackListener {
    fun onShipperLoadSuccess(shipperList:List<ShipperModel>)
    fun onShipperLoadSuccess(pos:Int, orderModel: OrderModel?,
                             shipperList: List<ShipperModel>?,
                             dialog: AlertDialog?,
                             ok: Button?, cancel: Button?,
                             rdi_shipping: RadioButton?,
                             rdi_shipped: RadioButton?,
                             rdi_cancelled: RadioButton?,
                             rdi_delete: RadioButton?,
                             rdi_restore_placed: RadioButton?)
    fun onShipperLoadFailed(message:String)
}