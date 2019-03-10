package at.htlleonding.mqttclient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

//class MainActivityViewModel : ViewModel() {
class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private val TAG = MainActivityViewModel::class.java.simpleName
        private val SERVER_URI = "openhabian12.fritz.box:1883"
        //private val SERVER_URI = "192.168.1.177:1883"
        private val TOPIC = "seminar/thing/#"
    }

    val myApplication = application
    val statusMessage: MutableLiveData<String> = MutableLiveData<String>()
    val connectBtnText: MutableLiveData<String> = MutableLiveData<String>()
    val isRgbLedOn: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val temperature: MutableLiveData<String> = MutableLiveData<String>()
    val humidity: MutableLiveData<String> = MutableLiveData<String>()
    val serverUri: MutableLiveData<String> = MutableLiveData<String>()
    val topic: MutableLiveData<String> = MutableLiveData<String>()

    init {
        statusMessage.value = State.DISCONNECTED.name
        connectBtnText.value = myApplication.getString(R.string.btn_txt_connect)
        isRgbLedOn.value = false
        temperature.value = "100.0"
        humidity.value = "20.0"
        serverUri.value = SERVER_URI
        topic.value = TOPIC

//        val stat = "not connected!"
//        statusMessage = Transformations.map(stat) { stat -> stat.value}
    }

    fun updateUIwithConnection(isConnected: Boolean, exception: String = "n/a") {
        //statusMessage.value = if (isConnected) State.CONNECTED.name else State.DISCONNECTED.name
        if (isConnected) {
            connectBtnText.value = myApplication.getString(R.string.btn_txt_disconnect)
            statusMessage.value = "Subscription to topic ${topic.value}"
        } else {
            myApplication.getString(R.string.btn_txt_connect)
            statusMessage.value = "Subscription failed to topic ${topic.value}: ${exception}"
        }
    }
}