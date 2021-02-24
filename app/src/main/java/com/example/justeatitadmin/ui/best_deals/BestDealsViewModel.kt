package com.example.justeatitadmin.ui.best_deals

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.justeatitadmin.Callback.IBestDealsCallbackListener
import com.example.justeatitadmin.Callback.ICategoryCallbackListener
import com.example.justeatitadmin.Common.Common
import com.example.justeatitadmin.Model.BestDealsModel
import com.example.justeatitadmin.Model.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BestDealsViewModel : ViewModel(), IBestDealsCallbackListener {

    private var bestDealsListMutable: MutableLiveData<List<BestDealsModel>>?=null
    private var messageError: MutableLiveData<String> = MutableLiveData()
    private val bestDealsCallbackListener: IBestDealsCallbackListener

    init {
        bestDealsCallbackListener = this
    }

    fun getBestDealsList():MutableLiveData<List<BestDealsModel>>{
        if (bestDealsListMutable == null)
        {
            bestDealsListMutable = MutableLiveData()
            loadBestDeals()
        }
        return bestDealsListMutable!!
    }

    fun loadBestDeals() {
        val tempList = ArrayList<BestDealsModel>()
        val bestDealsRef = FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
            .child(Common.currentServerUser!!.restaurant!!)
            .child(Common.BEST_DEALS)
        bestDealsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                bestDealsCallbackListener.onListBestDealsLoadFailed((p0.message))
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (itemSnapshot in p0.children){
                    val model = itemSnapshot.getValue<BestDealsModel>(BestDealsModel::class.java)
                    model!!.key = itemSnapshot.key!!
                    tempList.add(model)
                }
                bestDealsCallbackListener.onListBestDealsLoadSuccess(tempList)
            }

        })
    }

    fun getMessageError():MutableLiveData<String>{
        return messageError
    }

    override fun onListBestDealsLoadSuccess(bestDealsModels: List<BestDealsModel>) {
        bestDealsListMutable!!.value = bestDealsModels
    }

    override fun onListBestDealsLoadFailed(message: String) {
        messageError.value = message
    }

}
