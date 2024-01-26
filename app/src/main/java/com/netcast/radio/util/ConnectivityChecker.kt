import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager

class ConnectivityChecker(private val context: Context) {

    interface NetworkStateListener {
        fun onInternetAvailable()
        fun onInternetUnavailable()
    }

    private var listener: NetworkStateListener? = null

    private val connectivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateInternetConnection()
        }
    }

    fun setListener(listener: NetworkStateListener) {
        this.listener = listener
    }

    fun register() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(connectivityReceiver, filter)
    }

    fun unregister() {
        context.unregisterReceiver(connectivityReceiver)
    }

    private fun updateInternetConnection() {
        val isConnected = isInternetAvailable()
        if (isConnected) {
            listener?.onInternetAvailable()
        } else {
            listener?.onInternetUnavailable()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}
