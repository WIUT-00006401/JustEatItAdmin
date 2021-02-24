package com.example.justeatitadmin.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.justeatitadmin.Callback.ICategoryCallbackListener
import com.example.justeatitadmin.Common.Common
import com.example.justeatitadmin.Model.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoryViewModel : ViewModel(), ICategoryCallbackListener {

    override fun onCategoryLoadSuccess(categoriesList: List<CategoryModel>) {
        categoriesListMutable!!.value = categoriesList
    }

    override fun onCategoryLoadFailed(message: String) {
        messageError.value = message
    }

    private var categoriesListMutable:MutableLiveData<List<CategoryModel>>?=null
    private var messageError:MutableLiveData<String> = MutableLiveData()
    private val categoryCallbackListener: ICategoryCallbackListener

    init {
        categoryCallbackListener = this
    }

    fun getCategoryList():MutableLiveData<List<CategoryModel>>{
        if (categoriesListMutable == null)
        {
            categoriesListMutable = MutableLiveData()
            loadCategory()
        }
        return categoriesListMutable!!
    }

    fun getMessageError():MutableLiveData<String>{
        return messageError
    }

    fun loadCategory() {
        val tempList = ArrayList<CategoryModel>()
        val categoryRef = FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
            .child(Common.currentServerUser!!.restaurant!!)
            .child(Common.CATEGORY_REF)
        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                categoryCallbackListener.onCategoryLoadFailed((p0.message))
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (itemSnapshot in p0.children){
                    val model = itemSnapshot.getValue<CategoryModel>(CategoryModel::class.java)
                    model!!.menu_id = itemSnapshot.key
                    tempList.add(model)
                }
                categoryCallbackListener.onCategoryLoadSuccess(tempList)
            }

        })
    }

}