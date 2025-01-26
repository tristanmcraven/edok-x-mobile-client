package com.tristanmcraven.edokx.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.lifecycle.ViewModelProvider
import com.tristanmcraven.edok.utility.ApiClient
import com.tristanmcraven.edokx.R
import com.tristanmcraven.edokx.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var scrollViewContainer: LinearLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        scrollViewContainer = root.findViewById(R.id.scrollViewContainer)

        CoroutineScope(Dispatchers.IO).launch {
            val restaurants = ApiClient.IRestaurant.get()

            restaurants.forEach { rest ->
                val restaurantView = layoutInflater.inflate(R.layout.item_restaurant, scrollViewContainer, false)

                val restCategories = ApiClient.IRestaurant.getCategoriesById(rest.id)

                val imgRestaurant = restaurantView.findViewById<ImageView>(R.id.imgRestaurant)
                val textViewRestName = restaurantView.findViewById<TextView>(R.id.textViewRestName)
                val textViewDeliverTime = restaurantView.findViewById<TextView>(R.id.textViewDeliverTime)
                val textViewCategories = restaurantView.findViewById<TextView>(R.id.textViewCategories)

                imgRestaurant.setImageBitmap(BitmapFactory.decodeByteArray(rest.image, 0, rest.image.size))
                textViewRestName.text = rest.name
                textViewDeliverTime.text = "20-30 мин"
                textViewCategories.text = ""
                restCategories.forEach { cat ->
                    textViewCategories.append("$cat, ")
                }
                
            }
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}