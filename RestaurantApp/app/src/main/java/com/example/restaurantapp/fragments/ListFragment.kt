package com.example.restaurantapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp.R
import com.example.restaurantapp.adapter.RestaurantAdapter
import com.example.restaurantapp.data.Restaurant
import com.example.restaurantapp.data.RestaurantViewModel
import com.example.restaurantapp.repository.Repository
import com.example.restaurantapp.utils.Constants.Companion.PER_PAGE

class ListFragment : Fragment(), RestaurantAdapter.OnItemClickListener {

    private  lateinit var viewModel: ListViewModel
    private val adapter by lazy { RestaurantAdapter(this, this) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var lifecycleOwner: Fragment
    private val restaurantViewModel by lazy { activity?.application?.let { RestaurantViewModel(application = it) } }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_list, container, false)

        lifecycleOwner = this

        recyclerView = view.findViewById(R.id.recycler_view)
        setupRecyclerView()

        val repository = Repository()
        val viewModelFactory = ListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ListViewModel::class.java)

        val queryMap = mutableMapOf(Pair("country", "US"), Pair("per_page", PER_PAGE.toString()))

//        restaurantViewModel?.getFavoriteRestaurants()?.observe(this, {
//            adapter.setData(it, -1)
//        })
        updateData(queryMap)

        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                handleOnScroll(queryMap)
            }
        })

        return view
    }

    private fun handleOnScroll(queryMap: Map<String, String>){
        if(adapter.getPage() == -1){
            return
        }
        val count = recyclerView.layoutManager?.itemCount
        val lastVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition()
        val firstVisible = linearLayoutManager.findFirstCompletelyVisibleItemPosition()

        Log.d("Position", "first: $firstVisible; last: $lastVisible")

        if (count != null) {
            if (count-1 == lastVisible){
                val newQueryMap = queryMap.toMutableMap()
                newQueryMap["page"] = (adapter.getPage()+1).toString()

                updateData(newQueryMap)
                recyclerView.smoothScrollToPosition(1)
            }
        }

        if (0 == firstVisible && adapter.getPage() > 1){
            val newQueryMap = queryMap.toMutableMap()
            newQueryMap["page"] = (adapter.getPage()-1).toString()

            updateData(newQueryMap)

            if (count != null) {
                recyclerView.smoothScrollToPosition(count-2)
            }
        }
    }

    private fun updateData(queryMap: Map<String, String>){
        viewModel.getRestaurants(queryMap)
        viewModel.myResponse.observe(lifecycleOwner, { response ->
            if (response.isSuccessful){
                response.body()?.let { adapter.setData(it.restaurants, it.current_page) }
            }
            else {
                Toast.makeText(context, response.code(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(){
        recyclerView.adapter = adapter
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
    }

    override fun onItemClick(position: Int) {
        val details = DetailsFragment()

        val restaurant = adapter.getRestaurant(position)
        restaurantViewModel?.addRestaurant(restaurant)

        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, details)
                .addToBackStack(null)
                .commit()
    }

    override fun onFavoriteClicked(position: Int) {
        val favorite = recyclerView[position].findViewById<ImageView>(R.id.favorite)

        val restaurant = adapter.getRestaurant(position)

        if (restaurant.favorite){
            favorite.setImageResource(R.drawable.ic_not_favorite)

            restaurant.favorite = false

            if(!restaurant.new_image){
                restaurantViewModel?.deleteRestaurant(restaurant)
            }

            restaurantViewModel?.addRestaurant(restaurant)
        } else {
            favorite.setImageResource(R.drawable.ic_favorite)

            restaurant.favorite = true

            restaurantViewModel?.addRestaurant(restaurant)
        }
    }
}