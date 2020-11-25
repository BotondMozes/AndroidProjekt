package com.example.restaurantapp.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.restaurantapp.R
import com.example.restaurantapp.data.user.User
import com.example.restaurantapp.data.user.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    private val userViewModel by lazy { activity?.application?.let { UserViewModel(it) } }
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadDataFromRoom()

        addProfilePictureView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) ==
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

        editButton.setOnClickListener{
            toggleEditMode(true)
        }

        saveButton.setOnClickListener{
            if (saveUser()){
                toggleEditMode(false)
            }
        }
    }

    companion object {
        //image pick code
        private const val IMAGE_PICK_CODE = 1000;
        //Permission code
        private const val PERMISSION_CODE = 1001;
    }

    private fun loadDataFromRoom(){
        userViewModel?.getUser()?.observe(this, {
            if (it != null) {
                user = it
                toggleEditMode(false)
            } else{
                toggleEditMode(true)
            }
        })
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
            Glide.with(this)
                    .load(data?.data)
                    .into(profilePictureView)

            user!!.image = data?.data.toString()
            user?.let { userViewModel?.addUser(it) }
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        if (user == null){
            saveUser()
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    private fun toggleEditMode(toggle: Boolean){
        var edit = View.GONE
        var info = View.VISIBLE

        if (toggle){
            edit = View.VISIBLE
            info = View.GONE
        }

        // Edit view
        nameEditView.visibility = edit
        emailEditView.visibility = edit
        phoneEditView.visibility = edit
        addressEditView.visibility = edit
        saveButton.visibility = edit
        addProfilePictureView.visibility = edit

        // Info view
        nameView.visibility = info
        emailView.visibility = info
        phoneView.visibility = info
        addressView.visibility = info
        editButton.visibility = info
        favoritesButton.visibility = info

        if(user != null){
            nameEditView.setText(user!!.name)
            emailEditView.setText(user!!.email)
            phoneEditView.setText(user!!.phone)
            addressEditView.setText(user!!.address)

            nameView.text = user!!.name
            emailView.text = user!!.email
            phoneView.text = user!!.phone
            addressView.text = user!!.address

            Glide.with(this)
                .load(user!!.image.toUri())
                .into(profilePictureView)
        }
    }

    private fun saveUser(): Boolean{
        if(nameEditView.text.isEmpty() || emailEditView.text.isEmpty() || phoneEditView.text.isEmpty() || addressEditView.text.isEmpty()){
            Toast.makeText(context, "Please fill out all fields!", Toast.LENGTH_SHORT).show()

            return false
        } else {
            if (user == null) {
                user = User(0)
            }

            user!!.name = nameEditView.text.toString()
            user!!.email = emailEditView.text.toString()
            user!!.phone = phoneEditView.text.toString()
            user!!.address = addressEditView.text.toString()

            userViewModel?.addUser(user!!)
        }
        return true
    }
}