package at.htlleonding.mqttclient

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

//class MainActivityViewModel : ViewModel() {
class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private val TAG = MainActivityViewModel::class.java.simpleName
        private val SERVER_URI = "marvin2.fritz.box:50583"
        //private val SERVER_URI = "192.168.1.177:1883"
        val TOPIC = "seminar/28"
        val TOPIC_ALL = TOPIC + "/#"
    }

    val myApplication = application
    val statusMessage: MutableLiveData<String> = MutableLiveData<String>()
    val connectBtnText: MutableLiveData<String> = MutableLiveData<String>()
    val isRgbLedOn: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val rgbLedColor: MutableLiveData<String> = MutableLiveData<String>()
    val temperature: MutableLiveData<String> = MutableLiveData<String>()
    val humidity: MutableLiveData<String> = MutableLiveData<String>()
    val serverUri: MutableLiveData<String> = MutableLiveData<String>()
    val topic: MutableLiveData<String> = MutableLiveData<String>()

    init {
        updateUiDisconnected()
    }

    fun updateUiWithConnection(isConnected: Boolean, exception: String = "n/a") {
        if (isConnected) {
            connectBtnText.value = myApplication.getString(R.string.btn_txt_disconnect)
            statusMessage.value = "Subscription to topic ${topic.value}"
        } else {
            myApplication.getString(R.string.btn_txt_connect)
            statusMessage.value = "Subscription failed to topic ${topic.value}: ${exception}"
        }
    }

    fun updateUiDisconnected() {
        statusMessage.value = State.DISCONNECTED.name
        connectBtnText.value = myApplication.getString(R.string.btn_txt_connect)
        isRgbLedOn.value = false
        rgbLedColor.value = "990000"
        temperature.value = "100.0"
        humidity.value = "20.0"
        serverUri.value = SERVER_URI
        topic.value = TOPIC_ALL
    }

    fun updateUiTemperature(temperature: String) {
        this.temperature.value = temperature
    }

    fun updateUiHumidity(humidity: String) {
        this.humidity.value = humidity
    }

    /**
     * seminar/thing/pir: {"timestamp":1552207303,"value":1}
     * seminar/thing/temperature: {"timestamp":1552206244,"value":37.6}
     * seminar/thing/humidity: {"timestamp":1552206105,"value":35.7}
     * seminar/thing/rgbled/command: 99
     * seminar/thing/rgbled/command: 9900
     * seminar/thing/rgbled/command: 990000
     * seminar/thing/rgbled/state: 99 oder 9900 oder 990000
     */
    fun updateUi(topic: String, payload: MqttPayload) {
        when (topic) {
            "seminar/thing/temperature" -> updateUiTemperature(payload.value.toString())
            "seminar/thing/humidity" -> updateUiHumidity(payload.value.toString())
            "seminar/thing/rgbled/command" -> rgbLedColor.value = payload.value.toString()
            else -> Log.e(TAG, "${topic} / ${payload.value}")
        }
    }
}