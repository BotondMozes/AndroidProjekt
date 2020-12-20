package com.example.restaurantapp.fragments

import android.content.Context
import android.content.res.Resources
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantapp.R
import com.example.restaurantapp.adapter.RestaurantAdapter
import com.example.restaurantapp.data.restaurant.RestaurantViewModel
import com.example.restaurantapp.repository.Repository
import com.example.restaurantapp.utils.Constants.Companion.PER_PAGE
import kotlin.collections.Map
import kotlin.collections.isNotEmpty
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toMutableMap


class ListFragment : Fragment(), RestaurantAdapter.OnItemClickListener {

    private  lateinit var viewModel: ListViewModel
    private val adapter by lazy { RestaurantAdapter(this, this) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var lifecycleOwner: Fragment
    private val restaurantViewModel by lazy { activity?.application?.let { RestaurantViewModel(it) } }
    private val queryMap = mutableMapOf(Pair("per_page", PER_PAGE.toString()))

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_list, container, false)

        lifecycleOwner = this

        // setup recycler view
        recyclerView = view.findViewById(R.id.recycler_view)
        setupRecyclerView()

        val repository = Repository()
        val viewModelFactory = ListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ListViewModel::class.java)

        loadDataFromRoom(0)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handleOnScroll(queryMap)
            }
        })

        setCities(view.findViewById(R.id.autoCompleteTextView) as AutoCompleteTextView)

        view.findViewById<ImageView>(R.id.imageView4).setOnClickListener {
            toggleFavorites(it as ImageView, queryMap)
        }
        view.findViewById<ImageView>(R.id.imageView5).setOnClickListener {
            val searchBar = view.findViewById<CardView>(R.id.cardView2)
            toggleSearchBar(searchBar)
        }

        view.findViewById<Button>(R.id.button).setOnClickListener{
            adapter.setRoomMode(false)

            val name = view.findViewById<EditText>(R.id.searchView).text.toString()
            val city = view.findViewById<EditText>(R.id.autoCompleteTextView).text.toString()

            if (name == "" && city == ""){
                Toast.makeText(context, "Fill out at least one field!", Toast.LENGTH_SHORT).show()
            } else{
                queryMap["name"] = name
                queryMap["city"] = city

                queryMap["page"] = "1"

                adapter.setRoomMode(false)

                loadDataFromApi(queryMap)
                Glide.with(this)
                        .load(R.drawable.ic_not_favorite)
                        .into(view.findViewById(R.id.imageView4))
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val mode = sharedPref.getInt("Favorite", 0)

        if(mode == 1) {
            loadDataFromRoom(0)
            view!!.findViewById<CardView>(R.id.cardView2).visibility = GONE
        } else {
            view!!.findViewById<CardView>(R.id.cardView2).visibility = VISIBLE
        }
    }

    private fun setCities(view: AutoCompleteTextView){
        viewModel.getCities()
        viewModel.cities.observe(lifecycleOwner, { response ->
            if (response.isSuccessful) {
                val cities: List<String> = response.body()!!.cities
                val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this.context!!, android.R.layout.simple_dropdown_item_1line, cities)

                view.setAdapter(arrayAdapter);
            } else {
                Toast.makeText(context, response.code().toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleSearchBar(searchBar: CardView){
        if (searchBar.height == (40 * Resources.getSystem().displayMetrics.density).toInt()){
            searchBar.updateLayoutParams{
                height = (100 * Resources.getSystem().displayMetrics.density).toInt()
            }
            searchBar.findViewById<EditText>(R.id.searchView).visibility = View.VISIBLE
            searchBar.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView).visibility = View.VISIBLE
            searchBar.findViewById<Button>(R.id.button).visibility = View.VISIBLE
        } else {
            searchBar.updateLayoutParams{
                height = (40 * Resources.getSystem().displayMetrics.density).toInt()
            }
            searchBar.findViewById<EditText>(R.id.searchView).visibility = GONE
            searchBar.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView).visibility = GONE
            searchBar.findViewById<Button>(R.id.button).visibility = GONE
        }
    }

    private fun toggleFavorites(it: ImageView, queryMap: Map<String, String>){
        if (adapter.isRoomMode()){
            it.setImageResource(R.drawable.ic_not_favorite)
            adapter.setRoomMode(false)

            val newQueryMap = queryMap.toMutableMap()
            newQueryMap["page"] = "1"

            loadDataFromApi(newQueryMap)
        } else {
            it.setImageResource(R.drawable.ic_favorite)
            adapter.setRoomMode(true)

            loadDataFromRoom(0)
        }
    }

    private fun handleOnScroll(queryMap: Map<String, String>){
        val page = adapter.getPage()

        val visibleItemCount = linearLayoutManager.childCount
        val itemCount = recyclerView.layoutManager?.itemCount
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

        if( itemCount != null){
            if(adapter.isRoomMode()){
                // next page
                if (visibleItemCount + firstVisibleItemPosition >= itemCount-2 && firstVisibleItemPosition >= 0){
                    loadDataFromRoom(page + 1)
                }
            } else {
                // next page
                if (visibleItemCount + firstVisibleItemPosition >= itemCount-2 && firstVisibleItemPosition >= 0){
                    val newQueryMap = queryMap.toMutableMap()
                    newQueryMap["page"] = (page + 1).toString()

                    loadDataFromApi(newQueryMap)
                }
            }

        }
    }

    private fun loadDataFromRoom(page: Int){
        restaurantViewModel?.getFavoriteRestaurants(page, PER_PAGE)?.observe(this, {
            if (it.isNotEmpty()) {
                    adapter.setData(it, page)
            }
        })
    }

    private fun loadDataFromApi(queryMap: Map<String, String>){
        viewModel.getRestaurants(queryMap)
        viewModel.myResponse.observe(lifecycleOwner, { response ->
            if (response.isSuccessful) {
                val restaurants = response.body()?.restaurants?.toMutableList()
                val page = response.body()?.page
                if (restaurants!!.isNotEmpty()) {
                    for (res in restaurants){
                        restaurantViewModel?.getRestaurantById(res.id)?.observe(this, {
                            if (it != null) {
                                if(it.favorite){
                                    res.favorite = true
                                }
                                if(it.new_image){
                                    res.image_url = it.image_url
                                    res.new_image = true
                                }
                            }
                        })
                    }
                    page?.let { adapter.setData(restaurants, it) }
                }// else {
                 //   Toast.makeText(context, "No suitable restaurants!", Toast.LENGTH_SHORT).show()
                //}
            } else {
                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
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

        val bundle = Bundle()

        bundle.putLong("id", restaurant.id)
        details.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, details)
                .addToBackStack(null)
                .commit()
    }

    override fun onFavoriteClicked(position: Int) {
        val favorite = linearLayoutManager.findViewByPosition(position)?.findViewById<ImageView>(R.id.favorite)
        val restaurant = adapter.getRestaurant(position)

        if (restaurant.favorite){
            favorite?.setImageResource(R.drawable.ic_not_favorite)

            restaurant.favorite = false

            if(!restaurant.new_image){
                restaurantViewModel?.deleteRestaurant(restaurant)
            } else {
                restaurantViewModel?.addRestaurant(restaurant)
            }

            if (adapter.isRoomMode()){
                adapter.removeRestaurant(position)
            }
        } else {
            favorite?.setImageResource(R.drawable.ic_favorite)

            restaurant.favorite = true

            restaurantViewModel?.addRestaurant(restaurant)
        }
    }
}