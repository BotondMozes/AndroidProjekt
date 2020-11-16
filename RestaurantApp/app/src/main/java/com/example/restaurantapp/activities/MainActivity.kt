package com.example.restaurantapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.restaurantapp.R
import com.example.restaurantapp.databinding.ActivityMainBinding
import com.example.restaurantapp.fragments.ProfileFragment


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listFragment = com.example.restaurantapp.fragments.ListFragment()
        val profileFragment = ProfileFragment()

        makeCurrentFragment(listFragment)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.ic_restaurants -> makeCurrentFragment(listFragment)
                R.id.ic_profile -> makeCurrentFragment(profileFragment)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHolder, fragment)
            commit()
        }
}