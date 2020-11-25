package com.example.restaurantapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantapp.R
import com.example.restaurantapp.fragments.ListFragment
import com.example.restaurantapp.data.restaurant.Restaurant

class RestaurantAdapter(private val parent: ListFragment, private val listener: OnItemClickListener): RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>(){

    private var restaurantList: MutableList<Restaurant> = mutableListOf()
    private var page = 0
    private var room = true

    inner class RestaurantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val name: TextView = itemView.findViewById(R.id.restaurantName)
        val image: ImageView = itemView.findViewById(R.id.imageView)
        val address: TextView = itemView.findViewById(R.id.address)
        val money: TextView = itemView.findViewById(R.id.money)
        val favorite: ImageView = itemView.findViewById(R.id.favorite)


        init {
            itemView.setOnClickListener(this)
            favorite.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (v != null) {
                if (position != RecyclerView.NO_POSITION){
                    if (v.id == favorite.id){
                        listener.onFavoriteClicked(position)
                    } else {
                        listener.onItemClick(position)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        return RestaurantViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.restaurant_item, parent, false))
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.name.text = restaurantList[position].name
        holder.address.text = restaurantList[position].address
        holder.money.text = restaurantList[position].price.toString()

        if(restaurantList[position].favorite){
            Glide.with(parent)
                    .load(R.drawable.ic_favorite)
                    .into(holder.favorite)
        } else {
            Glide.with(parent)
                    .load(R.drawable.ic_not_favorite)
                    .into(holder.favorite)
        }

        Glide.with(parent)
            .load(restaurantList[position].image_url.toUri())
            .into(holder.image)
    }


    override fun getItemCount(): Int {
        return restaurantList.size
    }


    fun setData(newList: List<Restaurant>, newPage: Int){
        if(newList.isNotEmpty()){
            restaurantList.addAll(newList)

            restaurantList = restaurantList.distinct().toMutableList()

            page = newPage
            notifyDataSetChanged()
        }
    }

    fun getPage(): Int{
        return page
    }

    fun getRestaurant(position: Int): Restaurant {
        return restaurantList[position]
    }

    fun removeRestaurant(position: Int){
        restaurantList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun isRoomMode(): Boolean{
        return room
    }

    fun setRoomMode(mode: Boolean){
        if(mode){
            room = true
            page = 0
        } else{
            room = false
            page = 1
        }

        restaurantList = mutableListOf()
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onFavoriteClicked(position: Int)
    }

}