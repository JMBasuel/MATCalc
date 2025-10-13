package com.zinterr.matcalc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zinterr.matcalc.databinding.PrivacyPolicyBinding

class PrivacyPolicy : Fragment() {

    private lateinit var binding: PrivacyPolicyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PrivacyPolicyBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return binding.root
    }
}