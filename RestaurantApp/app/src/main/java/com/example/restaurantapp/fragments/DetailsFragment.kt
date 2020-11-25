package com.example.restaurantapp.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.restaurantapp.R
import com.example.restaurantapp.data.restaurant.Restaurant
import com.example.restaurantapp.data.restaurant.RestaurantViewModel
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

        view.findViewById<ImageView>(R.id.imageView7).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else{
                    //permission already granted
                    pickImageFromGallery();
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }
    }

    companion object {
        //image pick code
        private const val IMAGE_PICK_CODE = 1000;
        //Permission code
        private const val PERMISSION_CODE = 1001;
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
        val myPlace = LatLng(restaurant.lat.toDouble(), restaurant.lng.toDouble())

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

        if (!restaurant.favorite && !restaurant.new_image){
            restaurantViewModel?.deleteRestaurant(restaurant)
        }
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            val imageHolder = view!!.findViewById<ImageView>(R.id.imageView6)
            Glide.with(this)
                    .load(data?.data)
                    .into(imageHolder)

            restaurant.image_url = data?.data.toString()
            restaurant.new_image = true

            restaurantViewModel?.addRestaurant(restaurant)
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
}