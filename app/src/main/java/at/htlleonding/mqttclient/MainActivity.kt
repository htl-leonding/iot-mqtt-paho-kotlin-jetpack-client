package at.htlleonding.mqttclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val SERVER_URI = "tcp://openhabianpi12.fritz.box:1883"
    private val TOPIC = "house/livingroom/LightLivingRoom"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectMqtt()

        Log.d(TAG, "********************************************************************************************")
        Log.d(TAG, "finished")

    }

    private fun connectMqtt() {
        val clientId = MqttClient.generateClientId()
        val client = MqttAndroidClient(
            this.applicationContext,
            SERVER_URI,
            clientId
        )

        try {
            client.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    Log.d(TAG, "********************************************************************************************")
                    Log.d(TAG, "mqtt connected: will reconnect: ${reconnect}, server-uri: ${serverURI}")
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d(TAG, "********************************************************************************************")
                    Log.d(TAG, "mqtt connection lost: ${cause?.message ?: "n/a"}")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d(TAG, "********************************************************************************************")
                    Log.d(TAG, "mqtt message arrived: Topic: ${topic}, Message: ${message}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(TAG, "********************************************************************************************")
                    Log.d(TAG, "mqtt delivery complete")
                }

            })
        } catch (e: MqttException) {
            Log.e(TAG, e.message)
        }

        // client connect
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.setAutomaticReconnect(true)
        mqttConnectOptions.setCleanSession(false)
        //mqttConnectOptions.userName = ""
        //mqttConnectOptions.password = "".toCharArray()

        client.connect(mqttConnectOptions, this.applicationContext, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(TAG, "********************************************************************************************")
                Log.e(TAG, "onSuccess")
                subscribe(client)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(TAG, "********************************************************************************************")
                Log.e(TAG, "onFailure: ${exception?.message ?: "n/a"}")
            }
        })
    }

    private fun subscribe(client: MqttAndroidClient) {
        try {
            client.subscribe(TOPIC, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.d(
                        TAG,
                        "********************************************************************************************"
                    )
                    Log.w(TAG, "Subscription!")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.d(
                        TAG,
                        "********************************************************************************************"
                    )
                    Log.w(TAG, "Subscription fail!")
                }
            })
        } catch (e: MqttException) {
            System.err.println("Exception subscribing")
            e.printStackTrace()
        }
    }
}

