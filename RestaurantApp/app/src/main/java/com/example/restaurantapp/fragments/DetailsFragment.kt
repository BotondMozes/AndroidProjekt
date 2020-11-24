package com.example.restaurantapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.restaurantapp.R
import com.example.restaurantapp.data.Restaurant
import com.example.restaurantapp.data.RestaurantViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DetailsFragment : Fragment(), OnMapReadyCallback {
    private val restaurantViewModel by lazy { activity?.application?.let { RestaurantViewModel(it) } }
    private lateinit var restaurant: Restaurant

    private var mMapView: MapView? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMapView = view.findViewById(R.id.mapView)
        mMapView!!.onCreate(savedInstanceState)

        mMapView!!.onResume()

        try {
            MapsInitializer.initialize(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if(arguments?.getInt("id") != null){
            val id = arguments!!.getInt("id")

            loadData(id, view)
        }
    }

    private fun loadData(id: Int, view: View){
        restaurantViewModel?.getRestaurantById(id)?.observe(this, {
            if (it != null) {
                restaurant = it
                view.findViewById<TextView>(R.id.textView2).text = restaurant.name

                view.findViewById<TextView>(R.id.textView3).text = restaurant.country
                view.findViewById<TextView>(R.id.textView4).text = restaurant.city
                view.findViewById<TextView>(R.id.textView5).text = restaurant.address

                view.findViewById<TextView>(R.id.textView7).text = restaurant.mobile_reserve_url
                view.findViewById<TextView>(R.id.textView9).text = restaurant.phone

                val imageHolder = view.findViewById<ImageView>(R.id.imageView6)
                Glide.with(this)
                        .load(restaurant.image_url)
                        .into(imageHolder)

                mMapView!!.getMapAsync(this)
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        Log.d("LOG", restaurant.lat.toString())

        val myPlace = LatLng(restaurant.lat.toDouble(), restaurant.lng.toDouble())

        Log.d("LOG", myPlace.toString())

        googleMap?.addMarker(
                MarkerOptions()
                        .position(myPlace)
                        .title(restaurant.name)
        )
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(myPlace))
    }

    override fun onResume() {
        mMapView!!.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
    }

}