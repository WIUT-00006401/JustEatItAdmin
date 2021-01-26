package com.example.justeatitadmin.Callback

import com.example.justeatitadmin.Model.BestDealsModel

interface IBestDealsCallbackListener {
    fun onListBestDealsLoadSuccess(bestDealsModels: List<BestDealsModel>)
    fun onListBestDealsLoadFailed(message:String)
}