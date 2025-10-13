package com.zinterr.matcalc

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.zinterr.matcalc.databinding.CompoundBinding
import kotlin.math.pow

@SuppressLint("SetTextI18n")
class Compound : Fragment() {

    private lateinit var binding: CompoundBinding
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CompoundBinding.inflate(inflater, container, false)

        binding.btnCalculate.setOnClickListener {
            calculate()
        }

        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return binding.root
    }

    private fun calculate() {
        if (binding.edtPrincipal.text.toString() != "" && binding.edtRate.text.toString() != "" &&
            binding.edtTime.text.toString() != "") {
            if (binding.edtRate.text.toString().toDouble() <= 100) {
                val compoundFrequency = when (binding.spnFrequency.selectedItem.toString()) {
                    "Monthly" -> 12
                    "Quarterly" -> 4
                    "Semi-annually" -> 2
                    "Annually" -> 1
                    else -> 0
                }
                val compoundInterestEquation = "A = P(1 + r/n)^nt\n    ="
                val compoundPrincipal = "%,d".format(binding.edtPrincipal.text.toString().toInt())
                val compoundRate = binding.edtRate.text.toString().toDouble() / 100
                val compoundTime = binding.edtTime.text.toString().toDouble() / 12
                val futureValue = roundToIntNotString(binding.edtPrincipal.text.toString()
                        .toInt() * (1 + (compoundRate / compoundFrequency))
                    .pow(compoundFrequency * compoundTime))
                binding.tvResult.text = "$compoundInterestEquation $compoundPrincipal(1 + ${
                    roundToIntNot("%.4f".format(compoundRate).toDouble())
                }/$compoundFrequency)^$compoundFrequency(${
                    roundToIntNot("%.3f".format(compoundTime).toDouble())
                })\n    = $compoundPrincipal(1 + ${roundToIntNot(
                    "%.4f".format(compoundRate / compoundFrequency).toDouble())
                        })^${roundToIntNot(
                    "%.3f".format(compoundFrequency * compoundTime).toDouble())
                        }\n    = $compoundPrincipal(${roundToIntNot(
                    "%.4f".format((1 + (compoundRate / compoundFrequency)).pow(
                        compoundFrequency * compoundTime)).toDouble())
                        })\n    = $futureValue"
                binding.edtPrincipal.setText("")
                binding.edtPrincipal.requestFocus()
                binding.edtRate.setText("")
                binding.edtTime.setText("")
                binding.spnFrequency.setSelection(0)
                hideKeyboard()
            } else prompt("Invalid rate!")
        } else prompt("Missing data!")
    }

    private fun roundToIntNotString(x: Double): String {
        return if (x % 1.0 == 0.0) "%,d".format(x.toInt())
        else "%,.2f".format(x)
    }

    private fun roundToIntNot(x: Double): Any {
        return if (x % 1.0 == 0.0) x.toInt()
        else x
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun prompt(message: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.TOP or Gravity.CENTER, 0, 470)
        toast?.show()
    }
}