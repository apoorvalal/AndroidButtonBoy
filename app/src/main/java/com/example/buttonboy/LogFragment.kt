package com.example.buttonboy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class LogFragment : Fragment() {

    // Get a reference to the shared ViewModel
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((requireActivity().application as ButtonBoyApplication).database.eventDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log, container, false)
        val logButton: Button = view.findViewById(R.id.logButton)

        logButton.setOnClickListener {
            viewModel.logEvent()
        }

        return view
    }
}