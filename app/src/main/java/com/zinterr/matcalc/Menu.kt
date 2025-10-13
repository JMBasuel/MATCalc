package com.zinterr.matcalc

import android.graphics.Paint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zinterr.matcalc.databinding.MenuBinding

class Menu : Fragment() {

    private lateinit var binding: MenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MenuBinding.inflate(inflater, container, false)

        binding.btnBasic.setOnClickListener {
            findNavController().navigate(MenuDirections.actionMenuToBasic())
        }

        binding.btnFibonacci.setOnClickListener {
            findNavController().navigate(MenuDirections.actionMenuToFibonacci())
        }

        binding.btnFDT.setOnClickListener {
            findNavController().navigate(MenuDirections.actionMenuToDistribution())
        }

        binding.btnCentral.setOnClickListener {
            findNavController().navigate(MenuDirections.actionMenuToTendency())
        }

        binding.btnDispersion.setOnClickListener {
            findNavController().navigate(MenuDirections.actionMenuToDispersion())
        }

        binding.btnCorrelation.setOnClickListener {
            findNavController().navigate(MenuDirections.actionMenuToCorrelation())
        }

        binding.btnRegression.setOnClickListener {
            findNavController().navigate(MenuDirections.actionMenuToRegression())
        }

        binding.btnSimple.setOnClickListener {
            findNavController().navigate(MenuDirections.actionMenuToSimple())
        }

        binding.btnCompound.setOnClickListener {
            findNavController().navigate(MenuDirections.actionMenuToCompound())
        }

        binding.btnAmortization.setOnClickListener {
            findNavController().navigate(MenuDirections.actionMenuToAmortization())
        }

        binding.tvCredit.paintFlags = binding.tvCredit.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.tvCredit.setOnClickListener {
            findNavController().navigate(MenuDirections.actionMenuToPrivacyPolicy())
        }

        return binding.root
    }
}