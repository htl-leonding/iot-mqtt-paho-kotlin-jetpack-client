package at.htlleonding.mqttclient

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.util.*
import kotlinx.serialization.*
import kotlinx.serialization.Optional
import kotlinx.serialization.json.Json

class MqttManager(
    val connectionParams: MqttConnectionParams,
    val context: Context,
    val viewModel: MainActivityViewModel
) {

    companion object {
        private val TAG = MqttManager::class.java.simpleName
    }

    private var client: MqttAndroidClient? = MqttAndroidClient(
        context,
        connectionParams.host,
        connectionParams.clientId + id(context)
    )

    private var uniqueID: String? = null
    private val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"

    init {

        client?.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(b: Boolean, s: String) {
                Log.w(TAG, "mqtt-connect-complete: ${s}")
            }

            override fun connectionLost(throwable: Throwable) {
                Log.e(TAG, "mqtt-connection-lost")
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
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                try {

                    Log.w(TAG, "${topic}: ${mqttMessage}")
                    val jsonString = mqttMessage.toString()
                    Log.d(TAG,jsonString)
                    val myPayload = parseJsonPayload(jsonString)
                    viewModel.updateUi(topic, myPayload)
                } catch (ex: Exception) {
                    Log.e(TAG, "mqtt-message-arrived failure: ${ex.message}")
                }
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.i(TAG, "mqtt-delivery-complete")
            }
        })
    }

    fun parseJsonPayload(jsonString: String): MqttPayload {
        //var message = Json.parse(MqttPayload.serializer(), jsonString)
        var message = Json.nonstrict.parse(MqttPayload.serializer(), jsonString)
        Log.d(TAG, message.toString())
        return message
    }

    fun connect() {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.setAutomaticReconnect(true)
        mqttConnectOptions.setCleanSession(false)
        //mqttConnectOptions.setUserName(this.connectionParams.username)
        //mqttConnectOptions.setPassword(this.connectionParams.password.toCharArray())
        try {
            var params = this.connectionParams
            client?.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.setBufferEnabled(true)
                    disconnectedBufferOptions.setBufferSize(100)
                    disconnectedBufferOptions.setPersistBuffer(false)
                    disconnectedBufferOptions.setDeleteOldestMessages(false)
                    client?.setBufferOpts(disconnectedBufferOptions)
                    subscribe(params.topic)
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.w(TAG, "Failed to connect to: " + params.host + exception.toString())
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            client?.unregisterResources()
            client?.close()
            viewModel.updateUiDisconnected()

        } catch (ex: MqttException) {
            System.err.println("Exception disconnect")
            ex.printStackTrace()
        }
    }

    // Subscribe to topic
    fun subscribe(topic: String) {
        try {
            client?.subscribe(topic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.w(TAG, "Subscription to topic ${viewModel.topic}")
                    viewModel.updateUiWithConnection(true)

                }

                override fun onFailure(asyncActionToken: IMqttToken, ex: Throwable) {
                    Log.w(TAG, "Subscription failed: ${ex.message}")
                    viewModel.updateUiWithConnection(false, ex.message ?: "subscription failed")
                }
            })
        } catch (ex: MqttException) {
            System.err.println("Exception subscribing")
            ex.printStackTrace()
        }
    }

    // Unsubscribe the topic
    fun unsubscribe(topic: String) {

        try {
            client?.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    viewModel.statusMessage.value = "UnSubscribed to Topic ${viewModel.topic.value}"
                }

                override fun onFailure(asyncActionToken: IMqttToken?, ex: Throwable?) {
                    viewModel.statusMessage.value = "Failed to UnSubscribe to Topic ${viewModel.topic.value}"
                }

            })
        } catch (ex: MqttException) {
            System.err.println("Exception unsubscribe")
            ex.printStackTrace()
        }

    }

    fun publish(topic: String, message: String) {
        try {
            var msg = message
            client?.publish(
                topic,
                msg.toByteArray(),
                0,
                false,
                null,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.w(TAG, "Publish Success! ${topic} -> ${message}")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.w(TAG, "Publish Failed! ${topic} -> ${message}")
                    }

                })
        } catch (ex: MqttException) {
            System.err.println("Exception publishing")
            ex.printStackTrace()
        }
    }

    @Synchronized
    fun id(context: Context): String {
        if (uniqueID == null) {
            val sharedPrefs = context.getSharedPreferences(
                PREF_UNIQUE_ID, Context.MODE_PRIVATE
            )
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null)
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString()
                val editor = sharedPrefs.edit()
                editor.putString(PREF_UNIQUE_ID, uniqueID)
                editor.commit()
            }
        }
        return uniqueID!!
    }
}

data class MqttConnectionParams(
    val clientId: String,
    val host: String,
    val topic: String,
    val username: String,
    val password: String
)

@Serializable
data class MqttPayload(
    val timestamp: Long,
    val value: Double
)

