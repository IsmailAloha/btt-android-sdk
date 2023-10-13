package com.bluetriangle.android.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bluetriangle.android.demo.databinding.ActivityNetworkTestBinding

class NetworkTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding:ActivityNetworkTestBinding = DataBindingUtil.setContentView(this, R.layout.activity_network_test)
        title = "Network Tests"

        binding.lifecycleOwner = this
        binding.viewModel = getViewModel()
        binding.viewModel?.displayMessageToUI = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}