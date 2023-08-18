package com.bluetriangle.android.demo.kotlin

import android.content.ComponentCallbacks2
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.databinding.ActivityMemoryTestBinding
import com.bluetriangle.android.demo.getViewModel

class MemoryTestActivity : AppCompatActivity(), ComponentCallbacks2 {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMemoryTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.memory_test)

        binding.lifecycleOwner = this
        binding.viewModel = getViewModel()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        val warningLevelType = when(level) {
            TRIM_MEMORY_BACKGROUND -> "TRIM_MEMORY_BACKGROUND"
            TRIM_MEMORY_COMPLETE -> "TRIM_MEMORY_COMPLETE"
            TRIM_MEMORY_MODERATE -> "TRIM_MEMORY_MODERATE"
            TRIM_MEMORY_RUNNING_CRITICAL -> "TRIM_MEMORY_RUNNING_CRITICAL"
            TRIM_MEMORY_RUNNING_LOW -> "TRIM_MEMORY_RUNNING_LOW"
            TRIM_MEMORY_RUNNING_MODERATE -> "TRIM_MEMORY_RUNNING_MODERATE"
            TRIM_MEMORY_UI_HIDDEN -> "TRIM_MEMORY_UI_HIDDEN"
            else -> "UNKNOWN"
        }
        Log.d("MemoryWarning", warningLevelType)
    }
}