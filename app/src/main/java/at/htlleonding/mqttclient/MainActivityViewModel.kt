package at.htlleonding.mqttclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    companion object {
        private val TAG = MainActivityViewModel::class.java.simpleName
        private val SERVER_URI = "openhabianpi12.fritz.box:1883"
        private val TOPIC = "seminar/thing"

    }

    val status: MutableLiveData<String> = MutableLiveData<String>()
    val isRgbLedOn: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val temperature: MutableLiveData<Double> = MutableLiveData<Double>()
    val humidity: MutableLiveData<Double> = MutableLiveData<Double>()
    var serverUri: String = SERVER_URI
    var topic: String = TOPIC

    init {
        status.value = "Not connected"
        isRgbLedOn.value = false
        temperature.value = 100.0
        humidity.value = 20.0
    }
}