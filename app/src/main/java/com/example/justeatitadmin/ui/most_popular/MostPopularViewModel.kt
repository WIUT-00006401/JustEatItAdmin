package com.example.justeatitadmin.ui.most_popular

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.justeatitadmin.Callback.IBestDealsCallbackListener
import com.example.justeatitadmin.Callback.IMostPopularCallbackListener
import com.example.justeatitadmin.Common.Common
import com.example.justeatitadmin.Model.BestDealsModel
import com.example.justeatitadmin.Model.MostPopularModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MostPopularViewModel : ViewModel(), IMostPopularCallbackListener {

    private var mostPopularListMutable: MutableLiveData<List<MostPopularModel>>?=null
    private var messageError: MutableLiveData<String> = MutableLiveData()
    private val mostPopularCallbackListener: IMostPopularCallbackListener

    init {
        mostPopularCallbackListener = this
    }

    override fun onListMostPopularLoadSuccess(mostPopularModels: List<MostPopularModel>) {
        mostPopularListMutable!!.value = mostPopularModels
    }

    override fun onListMostPopularLoadFailed(message: String) {
        messageError.value = message
    }

    fun getMostPopulars():MutableLiveData<List<MostPopularModel>>{
        if (mostPopularListMutable == null)
        {
            mostPopularListMutable = MutableLiveData()
            loadMostPopulars()
        }
        return mostPopularListMutable!!
    }

    fun loadMostPopulars() {
        val tempList = ArrayList<MostPopularModel>()
        val mostPopularRef = FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
            .child(Common.currentServerUser!!.restaurant!!)
            .child(Common.MOST_POPULAR)
        mostPopularRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                mostPopularCallbackListener.onListMostPopularLoadFailed((p0.message))
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (itemSnapshot in p0.children){
                    val model = itemSnapshot.getValue<MostPopularModel>(MostPopularModel::class.java)
                    model!!.key = itemSnapshot.key!!
                    tempList.add(model)
                }
                mostPopularCallbackListener.onListMostPopularLoadSuccess(tempList)
            }

        })
    }

    fun getMessageError():MutableLiveData<String>{
        return messageError
    }


}
