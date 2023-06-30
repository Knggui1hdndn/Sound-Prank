package com.pranksound.fartsound.trollandjoke.funnyapp.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints

interface ListenNetwork {
    fun onChangeNetwork(string: String)
}

class ListensChangeNetwork(val listensChangeNetwork: ListenNetwork) : BroadcastReceiver()  {
    companion object {
        var isConnectNetwork = ""
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {//check xem có khả năng kết nối internet hay không
            isConnectNetwork = Constraints.CONNECTION_NETWORK
            listensChangeNetwork.onChangeNetwork(isConnectNetwork)
        } else {
            isConnectNetwork = Constraints.DISCONNECT_NETWORK
            listensChangeNetwork.onChangeNetwork(isConnectNetwork)
        }
    }


}