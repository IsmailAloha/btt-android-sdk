package com.bluetriangle.android.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bluetriangle.android.demo.databinding.ActivityHomeBinding
import com.bluetriangle.android.demo.java.JavaTestListActivity
import com.bluetriangle.android.demo.kotlin.KotlinTestListActivity
import com.bluetriangle.android.demo.tests.LaunchTestScenario

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)

        binding.networkTest.setOnClickListener {
            startActivity(NetworkTestActivity::class.java)
        }

        binding.btnJavaTest.setOnClickListener {
            startActivity(JavaTestListActivity::class.java)
        }

        binding.btnKotlinTest.setOnClickListener {
            startActivity(KotlinTestListActivity::class.java)
        }
        DemoApplication.checkLaunchTest(LaunchTestScenario.OnActivityCreate)
    }

    private fun startActivity(activityClass: Class<*>) {
        startActivity(
            Intent(
                this, activityClass
            )
        )
    }
}