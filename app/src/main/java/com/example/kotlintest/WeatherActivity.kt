package com.example.kotlintest

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.kotlintest.databinding.ActivityWeatherBinding
import com.squareup.picasso.Picasso
import java.util.*

class WeatherActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityWeatherBinding.inflate(layoutInflater) }
    private val model by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btLoad.setOnClickListener(this)
        binding.ivClear.setOnClickListener(this)
        binding.ivFire.setColorFilter(Color.RED)
        binding.ivFlag.setColorFilter(Color.BLUE)

        disableAll()

        model.errorMessage.observe(this) {
            disableAll()
            binding.tvError.isVisible = true
            binding.tvError.setText(R.string.errorNotFound)
        }

        model.weather.observe(this) { weather ->
            binding.tvError.isVisible = false

            binding.tvCity.text = weather?.name
            binding.tvState.text = weather?.data?.get(0)?.description?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            binding.tvTemp.text = "${weather?.temperature?.temp}°"
            binding.tvDetails.text =
                "( ${weather?.temperature?.temp_min}° / ${weather?.temperature?.temp_max}° )"
            binding.tvWind.text = "${weather?.wind?.speed} km/h"

            enable(binding.tvCity)
            enable(binding.tvState)
            enable(binding.tvTemp)
            enable(binding.tvDetails)
            enable(binding.tvWind)

            binding.ivClear.isVisible = true
            binding.ivFire.isVisible = true
            binding.ivFlag.isVisible = true
            binding.ivWeather.isVisible = true

            println("https://openweathermap.org/img/wn/%s.png".format(weather?.data?.get(0)?.icon))
            Picasso.get().load("https://openweathermap.org/img/wn/%s.png".format(weather?.data?.get(0)?.icon)).into(binding.ivWeather)
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.ivClear -> {
                disableAll()
                binding.etCity.setText("")
            }
            binding.btLoad -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                    showWeather()
                } else {
                    // TODO: Call onRequestPermissionsResult function (4)
                }
            }
        }
    }

    private fun enable(element: TextView) {
        element.isVisible = true
    }

    private fun disableAll() {
        val elementsToClear = arrayListOf(
            binding.progressBar,
            binding.tvError,
            binding.ivWeather,
            binding.tvError,
            binding.tvCity,
            binding.tvTemp,
            binding.tvDetails,
            binding.tvWind,
            binding.tvState,
            binding.ivClear,
            binding.ivFire,
            binding.ivFlag
        )

        for (element in elementsToClear) {
            element.isVisible = false
        }
    }

    private fun showWeather() {
        println("show weather")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            showWeather()
        } else {
            Toast.makeText(this, "Need permission location", Toast.LENGTH_SHORT).show()
        }
    }
}
