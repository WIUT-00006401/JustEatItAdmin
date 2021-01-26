package com.example.justeatitadmin.Callback

import com.example.justeatitadmin.Model.BestDealsModel
import com.example.justeatitadmin.Model.MostPopularModel

interface IMostPopularCallbackListener {
    fun onListMostPopularLoadSuccess(mostPopularModels: List<MostPopularModel>)
    fun onListMostPopularLoadFailed(message:String)
}