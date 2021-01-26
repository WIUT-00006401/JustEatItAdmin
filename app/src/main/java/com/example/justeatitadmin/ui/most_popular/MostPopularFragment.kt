package com.example.justeatitadmin.ui.most_popular

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.justeatitadmin.Adapter.MyBestDealsAdapter
import com.example.justeatitadmin.Adapter.MyMostPopularAdapter
import com.example.justeatitadmin.Model.BestDealsModel
import com.example.justeatitadmin.Model.MostPopularModel

import com.example.justeatitadmin.R
import com.example.justeatitadmin.ui.best_deals.BestDealsViewModel
import dmax.dialog.SpotsDialog

class MostPopularFragment : Fragment() {


    private lateinit var viewModel: MostPopularViewModel

    private lateinit var dialog: AlertDialog
    private lateinit var layoutAnimationController: LayoutAnimationController
    private var adapter: MyMostPopularAdapter? = null

    private var recycler_most_popular: RecyclerView?=null

    internal var mostPopularModels:List<MostPopularModel> = ArrayList<MostPopularModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(MostPopularViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_most_popular, container, false)

        initViews(root)

        viewModel.getMessageError().observe(this, Observer {
            Toast.makeText(context,it, Toast.LENGTH_SHORT).show()
        })

        viewModel.getMostPopulars().observe(this, Observer {
            dialog.dismiss()
            mostPopularModels = it
            adapter = MyMostPopularAdapter(context!!,mostPopularModels)
            recycler_most_popular!!.adapter = adapter
            recycler_most_popular!!.layoutAnimation = layoutAnimationController
        })

        return root
    }

    private fun initViews(root: View?) {

        dialog = SpotsDialog.Builder().setContext(context)
            .setCancelable(false).build()
        dialog.show()
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)

        recycler_most_popular = root!!.findViewById(R.id.recycler_most_popular) as RecyclerView
        recycler_most_popular!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)

        recycler_most_popular!!.layoutManager = layoutManager
        recycler_most_popular!!.addItemDecoration(DividerItemDecoration(context,layoutManager.orientation))

    }


}