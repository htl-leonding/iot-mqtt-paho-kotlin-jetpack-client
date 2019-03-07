package at.htlleonding.mqttclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var model: MainActivityViewModel
    private var mqttManager: MqttManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProviders
            .of(this)
            .get(MainActivityViewModel::class.java)

        btn_connect.setOnClickListener {
            Toast.makeText(this, "CLICKED", Toast.LENGTH_SHORT).show()

        }

        updateUI()
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun updateUI() {
        tv_status_message.text = model.status.value
        et_server_uri.text = model.serverUri.toEditable()
        et_topic.text = model.topic.toEditable()
    }

    fun connect(view: View) {

        if (!(et_server_uri.text.isNullOrEmpty() && et_topic.text.isNullOrEmpty())) {
            var host = "tcp://" + et_server_uri.toString()
            var topic = et_topic.text.toString()
            var connectionParams = MqttConnectionParams(
                "MQTTSample",
                host,
                topic,
                "",
                ""
            )
            mqttManager = MqttManager(connectionParams, applicationContext)
            mqttManager?.connect()
        } else {
            tv_status_message.text = "Please enter all valid fields"
        }

    }

/*
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
                    Log.d(
                        TAG,
                        "********************************************************************************************"
                    )
                    Log.d(TAG, "mqtt connected: will reconnect: ${reconnect}, server-uri: ${serverURI}")
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d(
                        TAG,
                        "********************************************************************************************"
                    )
                    Log.d(TAG, "mqtt connection lost: ${cause?.message ?: "n/a"}")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d(
                        TAG,
                        "********************************************************************************************"
                    )
                    Log.d(TAG, "mqtt message arrived: Topic: ${topic}, Message: ${message}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(
                        TAG,
                        "********************************************************************************************"
                    )
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
                Log.d(
                    TAG,
                    "********************************************************************************************"
                )
                Log.e(TAG, "onSuccess")
                subscribe(client)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(
                    TAG,
                    "********************************************************************************************"
                )
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
    */
}

