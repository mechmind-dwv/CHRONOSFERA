package com.example

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class MainViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val magneticSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val noaaRepository = NoaaRepository()

    private val _magneticFieldValues = MutableStateFlow(FloatArray(3))
    val magneticFieldValues: StateFlow<FloatArray> = _magneticFieldValues.asStateFlow()

    private val _dissipativeLoadIndex = MutableStateFlow(0f)
    val dissipativeLoadIndex: StateFlow<Float> = _dissipativeLoadIndex.asStateFlow()

    private val _kpIndex = MutableStateFlow<String?>("Loading...")
    val kpIndex: StateFlow<String?> = _kpIndex.asStateFlow()

    private var exposureTimeSeconds = 0L

    init {
        startSensing()
        fetchKpIndex()
        trackExposure()
    }

    private fun startSensing() {
        magneticSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun trackExposure() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                exposureTimeSeconds++
                calculateIndex()
            }
        }
    }

    private fun fetchKpIndex() {
        viewModelScope.launch {
            while (true) {
                val kp = noaaRepository.getLatestKpIndex()
                if (kp != null) {
                    _kpIndex.value = kp
                } else if (_kpIndex.value == "Loading...") {
                    _kpIndex.value = "Unavailable"
                }
                delay(600000) // update every 10 minutes
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val values = event.values.clone()
            _magneticFieldValues.value = values
            calculateIndex()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun calculateIndex() {
        val vals = _magneticFieldValues.value
        val magnitude = sqrt((vals[0] * vals[0] + vals[1] * vals[1] + vals[2] * vals[2]).toDouble()).toFloat()
        
        // Equation from documentation:
        // (Magnitud_Campo_Magnético_Ambiental × 0.5) + (Intensidad_Senal_RF_Normalizada × 0.3) + (Tiempo_Exposición_Acumulado × 0.2)
        // We approximate RF with a base ambient noise factor of 10 if we can't read it.
        val baseRfIntensidad = 10f 
        val exposureFactor = (exposureTimeSeconds / 60f).coerceAtMost(50f) // cap exposure factor

        val index = (magnitude * 0.5f) + (baseRfIntensidad * 0.3f) + (exposureFactor * 0.2f)
        _dissipativeLoadIndex.value = index
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
