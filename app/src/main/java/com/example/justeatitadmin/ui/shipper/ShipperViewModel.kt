package com.example.justeatitadmin.ui.shipper

import android.app.AlertDialog
import android.widget.Button
import android.widget.RadioButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.justeatitadmin.Callback.IShipperLoadCallbackListener
import com.example.justeatitadmin.Common.Common
import com.example.justeatitadmin.Model.OrderModel
import com.example.justeatitadmin.Model.ShipperModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShipperViewModel : ViewModel(), IShipperLoadCallbackListener {
    private var shipperListMutable: MutableLiveData<List<ShipperModel>>?=null
    private var messageError: MutableLiveData<String> = MutableLiveData()
    private val shipperCallbackListener: IShipperLoadCallbackListener

    init {
        shipperCallbackListener = this
    }

    fun getShipperList(): MutableLiveData<List<ShipperModel>> {
        if (shipperListMutable == null)
        {
            shipperListMutable = MutableLiveData()
            loadShipper()
        }
        return shipperListMutable!!
    }

    private fun loadShipper() {
        val tempList = ArrayList<ShipperModel>()
        val shipperRef = FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
            .child(Common.currentServerUser!!.restaurant!!)
            .child(Common.SHIPPER_REF)
        shipperRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                shipperCallbackListener.onShipperLoadFailed((p0.message))
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (itemSnapshot in p0.children){
                    val model = itemSnapshot.getValue<ShipperModel>(ShipperModel::class.java)
                    model!!.key = itemSnapshot.key
                    tempList.add(model)
                }
                shipperCallbackListener.onShipperLoadSuccess(tempList)
            }


        })
    }

    fun getMessageError(): MutableLiveData<String> {
        return messageError
    }

    override fun onShipperLoadSuccess(shipperList: List<ShipperModel>) {
        shipperListMutable!!.value = shipperList
    }

    override fun onShipperLoadSuccess(
        pos: Int,
        orderModel: OrderModel?,
        shipperList: List<ShipperModel>?,
        dialog: AlertDialog?,
        ok: Button?,
        cancel: Button?,
        rdi_shipping: RadioButton?,
        rdi_shipped: RadioButton?,
        rdi_cancelled: RadioButton?,
        rdi_delete: RadioButton?,
        rdi_restore_placed: RadioButton?
    ) {
        //Nothing
    }

    override fun onShipperLoadFailed(message: String) {
        messageError!!.value = message
    }

}