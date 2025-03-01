package com.tristanmcraven.edokx.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tristanmcraven.edok.model.Order
import com.tristanmcraven.edok.utility.ApiClient
import com.tristanmcraven.edokx.OrderActivity
import com.tristanmcraven.edokx.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderHistoryAdapter(
    private val orders: List<Order>
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewOrderTime: TextView = view.findViewById(R.id.textViewOrderTime)
        val textViewOrderPrice: TextView = view.findViewById(R.id.textViewOrderPrice)
        val cardViewMain: CardView = view.findViewById(R.id.cardViewMain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = orders[position]
        val context = holder.itemView.context

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val orderTime = LocalDateTime.parse(item.createdAt, formatter)
        val hour = orderTime.hour
        val minute = orderTime.minute

        holder.textViewOrderTime.text = when (hour) {
            in 0 until 6 -> "Заказ ночью, в $hour:${String.format("%02d", minute)}"
            in 6 until 11 -> "Заказ утром, в $hour:${String.format("%02d", minute)}"
            in 11 until 18 -> "Заказ днём, в $hour:${String.format("%02d", minute)}"
            in 18..23 -> "Заказ вечером, в $hour:${String.format("%02d", minute)}"
            else -> ""
        }

        CoroutineScope(Dispatchers.IO).launch {
            val rest = ApiClient.IRestaurant.getById(item.restaurantId)!!
            withContext(Dispatchers.Main) {
                holder.textViewOrderPrice.text = "${item.total}₽, ${rest.name}"
            }
        }

        holder.cardViewMain.setOnClickListener {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra("orderNumber", item.id.toInt())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orders.size

}