package com.example.justeatitadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.justeatitadmin.Callback.IRecyclerItemClickListener
import com.example.justeatitadmin.Common.Common
import com.example.justeatitadmin.Model.FoodModel
import com.example.justeatitadmin.R
import java.lang.StringBuilder

class MyFoodListAdapter (internal var context: Context,
                         internal var foodList: List<FoodModel>):
    RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder>() {



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(foodList.get(position).image).into(holder.img_food_image!!)
        holder.txt_food_name!!.setText(foodList.get(position).name)
        holder.txt_food_price!!.setText(StringBuilder("$").append(foodList.get(position).price.toString()))

        //Event
        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.foodSelected = foodList.get(pos)
                Common.foodSelected!!.key = pos.toString()
            }
        })
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFoodListAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_item,parent,false))
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun getItemAtPosition(pos: Int): FoodModel {
        return foodList.get(pos)
    }

    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!,adapterPosition)
        }

        var txt_food_name: TextView?=null
        var txt_food_price: TextView?=null
        var img_food_image: ImageView?=null


        internal var listener: IRecyclerItemClickListener?=null

        fun setListener(listener: IRecyclerItemClickListener)
        {
            this.listener = listener
        }

        init {
            txt_food_name = itemView.findViewById(R.id.txt_food_name) as TextView
            txt_food_price = itemView.findViewById(R.id.txt_food_price) as TextView
            img_food_image = itemView.findViewById(R.id.img_food_image) as ImageView

            itemView.setOnClickListener(this)
        }
    }
}