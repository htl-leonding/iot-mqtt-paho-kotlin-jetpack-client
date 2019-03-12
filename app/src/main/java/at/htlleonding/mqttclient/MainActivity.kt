package at.htlleonding.mqttclient

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import at.htlleonding.mqttclient.databinding.ActivityMainBinding
import com.sdsmdg.harjot.vectormaster.VectorMasterView
import com.sdsmdg.harjot.vectormaster.models.PathModel
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.listeners.ColorListener
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import kotlin.math.min


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var model: MainActivityViewModel
    private var mqttManager: MqttManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        model = ViewModelProviders
            .of(this)
            .get(MainActivityViewModel::class.java)

        // https://proandroiddev.com/advanced-data-binding-binding-to-livedata-one-and-two-way-binding-dae1cd68530f
        val binding: ActivityMainBinding = DataBindingUtil
            .setContentView(this, R.layout.activity_main)

        binding.setLifecycleOwner(this)

        binding.viewmodel = model
        // https://developer.android.com/topic/libraries/data-binding/architecture#viewmodel

        btn_connect.setOnClickListener {
            if (model.connectBtnText.value == getString(R.string.btn_txt_connect)) {
                connect()
            } else {
                mqttManager?.unsubscribe(model.topic.value ?: "#")
                mqttManager?.disconnect()
            }
            Toast.makeText(this, "CLICKED", Toast.LENGTH_SHORT).show()
            //binding.btnConnect.setText(R.string.txt_unconnect)
        }

        tv_rgb_led_color.addTextChangedListener {
            //Toast.makeText(this, model.rgbLedColor.value, Toast.LENGTH_SHORT).show()
            //changeBulbColor(model.rgbLedColor.value!!.toInt())
            Log.d(TAG, "tv_rgb_led_color.addTextChangedListener")
            if (mqttManager != null) {
                mqttManager!!.publish("${MainActivityViewModel.TOPIC}/rgbled/command", model.rgbLedColor.value.toString())
            }
        }

        // https://blog.kotlin-academy.com/programmer-dictionary-event-listener-vs-event-handler-305c667d0e3c
        cpv_color_picker_view.setColorListener(object : ColorEnvelopeListener {
            override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                Log.d(TAG, "colorPickerView-colorListener #" + envelope!!.hexCode);
                model.rgbLedColor.value = String.format(
                    "%02d%02d%02d",
                    envelope.argb[1] / 255 * 99,
                    envelope.argb[2] / 255 * 99,
                    envelope.argb[3] / 255 * 99
                )
                Log.d(TAG, model.rgbLedColor.value);
                changeBulbColor(envelope.hexCode)
            }
        })
    }


    // Extension function
//    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
//
//    fun updateUI() {
//        tv_status_message.text = model.statusMessage.value
//        et_server_uri.text = model.serverUri.toEditable()
//        et_topic.text = model.topic.toEditable()
//    }


    fun changeBulbColor(color: String) {
        //val bulbVector: VectorMasterView by lazy { R.id.cpv_color_picker_view as VectorMasterView }
        val bulbVector: VectorMasterView = findViewById(R.id.iv_rgb_led)
        val bulbPath = bulbVector.getPathModelByName("bulb_path")
        Log.d(TAG, "changeBulbColor: ${color}")
        bulbPath.fillColor = Color.parseColor("#" + color)
    }


    fun connect() {

        if (!(et_server_uri.text.isNullOrEmpty() && et_topic.text.isNullOrEmpty())) {
            var host = "tcp://" + et_server_uri.text.toString()
            var topic = et_topic.text.toString()
            var connectionParams = MqttConnectionParams(
                "MQTTSample",
                host,
                topic,
                "",
                ""
            )
            mqttManager = MqttManager(connectionParams, applicationContext, model)
            mqttManager?.connect()
        } else {
            tv_status_message.text = "Please enter all valid fields"
        }

    }
}

