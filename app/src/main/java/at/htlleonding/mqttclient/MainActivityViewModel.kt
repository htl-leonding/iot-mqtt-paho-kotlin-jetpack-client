package at.htlleonding.mqttclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    companion object {
        private val TAG = MainActivityViewModel::class.java.simpleName
        //private val SERVER_URI = "openhabianpi12.fritz.box:1883"
        private val SERVER_URI = "192.168.1.177:1883"
        private val TOPIC = "seminar/thing"

    }

    val status: MutableLiveData<String> = MutableLiveData<String>()
    val isRgbLedOn: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val temperature: MutableLiveData<String> = MutableLiveData<String>()
    val humidity: MutableLiveData<String> = MutableLiveData<String>()
    val serverUri: MutableLiveData<String> = MutableLiveData<String>()
    val topic: MutableLiveData<String> = MutableLiveData<String>()

    init {
        status.value = "Not connected"
        isRgbLedOn.value = false
        temperature.value = "100.0"
        humidity.value = "20.0"
        serverUri.value = SERVER_URI
        topic.value = TOPIC

//        val stat = "not connected!"
//        status = Transformations.map(stat) { stat -> stat.value}
    }
}