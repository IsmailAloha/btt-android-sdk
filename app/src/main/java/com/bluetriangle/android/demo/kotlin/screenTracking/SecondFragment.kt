package com.bluetriangle.android.demo.kotlin.screenTracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.databinding.DialogAlertBinding
import com.google.android.material.button.MaterialButton

class SecondFragment : Fragment() {
    companion object {
        fun newInstance() = SecondFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_screen_tracking_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.tabViewScreen).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, TabViewFragment.newInstance())
                ?.addToBackStack(TabViewFragment::class.java.simpleName)
                ?.commit()
        }

        view.findViewById<MaterialButton>(R.id.showAlert).setOnClickListener {
            showDialog()
        }

        view.findViewById<MaterialButton>(R.id.showDialog).setOnClickListener {
            TestDialog().show(parentFragmentManager)
        }

        view.findViewById<MaterialButton>(R.id.showBottomSheet).setOnClickListener {
            BottomSheetDialog().show(parentFragmentManager)
        }
    }

    private fun showDialog() {
        val dialogBinding: DialogAlertBinding? =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_alert,
                null,
                false
            )

        val customDialog = AlertDialog.Builder(requireContext(), 0).create()
        dialogBinding?.btnOk?.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.apply {
            setView(dialogBinding?.root)
            setCancelable(false)
        }.show()
    }
}