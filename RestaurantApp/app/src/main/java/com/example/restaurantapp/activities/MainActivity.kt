package com.example.restaurantapp.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.restaurantapp.R
import com.example.restaurantapp.databinding.ActivityMainBinding
import com.example.restaurantapp.fragments.ProfileFragment


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val listFragment = com.example.restaurantapp.fragments.ListFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return

        makeCurrentFragment(listFragment)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            with (sharedPref.edit()) {
                putInt("Favorite", 0)
                apply()
            }

            when (it.itemId){
                R.id.ic_restaurants -> makeCurrentFragment(listFragment)
                R.id.ic_profile -> makeCurrentFragment(profileFragment)
            }
            true
        }
    }

    fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHolder, fragment)
            commit()
        }
}