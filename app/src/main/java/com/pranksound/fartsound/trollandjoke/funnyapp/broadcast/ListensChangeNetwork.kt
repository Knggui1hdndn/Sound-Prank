package com.pranksound.fartsound.trollandjoke.funnyapp.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints


class ListensChangeNetwork : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {//check xem có khả năng kết nối internet hay không
            intent!!.action = Constraints.CONNECTION_NETWORK
            context.sendBroadcast(intent)

            // Mạng đã được bật
            Toast.makeText(context, "Mạng đã được bật", Toast.LENGTH_SHORT).show()
        } else {
            intent!!.action = Constraints.DISCONNECT_NETWORK
            context.sendBroadcast(intent)

            // Mạng đã bị tắt
            Toast.makeText(context, "Mạng đã bị tắt", Toast.LENGTH_SHORT).show()
        }
    }
}