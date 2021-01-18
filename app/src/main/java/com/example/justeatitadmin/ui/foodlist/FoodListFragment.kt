package com.example.justeatitadmin.ui.foodlist

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.justeatitadmin.R
import com.example.justeatitadmin.Adapter.MyCategoriesAdapter
import com.example.justeatitadmin.Adapter.MyFoodListAdapter
import com.example.justeatitadmin.Callback.IMyButtonCallback
import com.example.justeatitadmin.Common.Common
import com.example.justeatitadmin.Common.MySwipeHelper
import com.example.justeatitadmin.Common.SpacesItemDecoration
import com.example.justeatitadmin.EventBus.ToastEvent
import com.example.justeatitadmin.Model.CategoryModel
import com.example.justeatitadmin.Model.FoodModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FoodListFragment : Fragment() {

    private var imageUri: Uri?=null
    private val PICK_IMAGE_REQUEST: Int=1234
    private lateinit var foodListViewModel: FoodListViewModel

    var recycler_food_list : RecyclerView?=null
    var layoutAnimationController: LayoutAnimationController?=null

    var adapter: MyFoodListAdapter?=null
    var foodModelList:List<FoodModel> = ArrayList<FoodModel>()

    //Variable
    private var img_food: ImageView?=null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var dialog:android.app.AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foodListViewModel =
            ViewModelProviders.of(this).get(FoodListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_food_list, container, false)
        initViews(root)
        foodListViewModel.getMutableFoodModelListData().observe(this, Observer {
            //if (it != null){
                //foodModelList = it
                //adapter = MyFoodListAdapter(context!!, foodModelList)
                adapter = MyFoodListAdapter(context!!, it)
                recycler_food_list!!.adapter = adapter
                recycler_food_list!!.layoutAnimation = layoutAnimationController
            //}
        })
        return root
    }

    private fun initViews(root: View?) {

        /*setHasOptionsMenu(true)

        dialog = SpotsDialog.Builder().setContext(context!!).setCancelable(false).build()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference*/

        recycler_food_list = root!!.findViewById(R.id.recycler_food_list) as RecyclerView
        recycler_food_list!!.setHasFixedSize(true)
        recycler_food_list!!.layoutManager = LinearLayoutManager(context)

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)

        (activity as AppCompatActivity).supportActionBar!!.title = Common.categorySelected!!.name

       /* val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels*/

        /*val swipe = object : MySwipeHelper(context!!,recycler_food_list!!,width/6)
        {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(context!!,
                    "Delete",
                    30,
                    0,
                    Color.parseColor("#9b0000"),
                    object : IMyButtonCallback {
                        override fun onClick(pos: Int){
                            Common.foodSelected = foodModelList[pos]
                            val builder = AlertDialog.Builder(context!!)
                            builder.setTitle("Delete")
                                .setMessage("Do you really want to delete food?")
                                .setNegativeButton("CANCEL",{dialogInterface, _-> dialogInterface.dismiss()})
                                .setPositiveButton("DELETE", {dialogInterface, _->
                                    val foodModel = adapter!!.getItemAtPosition(pos)
                                    if (foodModel.positionInList == -1)
                                        Common.categorySelected!!.foods!!.removeAt(pos)
                                    else
                                        Common.categorySelected!!.foods!!.removeAt(foodModel.positionInList)
                                    updateFood(Common.categorySelected!!.foods,true)
                                })
                            val deleteDialog = builder.create()
                            deleteDialog.show()
                        }

                    })
                )
                buffer.add(MyButton(context!!,
                    "Update",
                    30,
                    0,
                    Color.parseColor("#560027"),
                    object : IMyButtonCallback {
                        override fun onClick(pos: Int){
                            val foodModel = adapter!!.getItemAtPosition(pos)
                            if (foodModel.positionInList == -1)
                                showUpdateDialog(pos,foodModel)
                            else
                                showUpdateDialog(foodModel.positionInList, foodModel)
                        }

                    })
                )
                buffer.add(MyButton(context!!,
                    "Size",
                    30,
                    0,
                    Color.parseColor("#12005e"),
                    object : IMyButtonCallback {
                        override fun onClick(pos: Int){

                            val foodModel = adapter!!.getItemAtPosition(pos)
                            if (foodModel.positionInList == -1)
                                Common.foodSelected = foodModelList!![pos]
                            else
                                Common.foodSelected = foodModel
                            startActivity(Intent(context, SizeAddonEditActivity::class.java))
                            if (foodModel.positionInList == -1)
                                EventBus.getDefault().postSticky(AddonSizeEditEvent(false,pos))
                            else
                                EventBus.getDefault().postSticky(AddonSizeEditEvent(false,foodModel.positionInList))
                        }
                    })
                )
                buffer.add(MyButton(context!!,
                    "Addon",
                    30,
                    0,
                    Color.parseColor("#333639"),
                    object : IMyButtonCallback {
                        override fun onClick(pos: Int){
                            val foodModel = adapter!!.getItemAtPosition(pos)
                            if (foodModel.positionInList == -1)
                                Common.foodSelected = foodModelList!![pos]
                            else
                                Common.foodSelected = foodModel
                            startActivity(Intent(context, SizeAddonEditActivity::class.java))
                            if (foodModel.positionInList == -1)
                                EventBus.getDefault().postSticky(AddonSizeEditEvent(true,pos))
                            else
                                EventBus.getDefault().postSticky(AddonSizeEditEvent(true,foodModel.positionInList))

                        }

                    })
                )
            }

        }*/
    }


}