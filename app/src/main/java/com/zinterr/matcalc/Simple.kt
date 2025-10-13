package com.zinterr.matcalc

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.zinterr.matcalc.databinding.SimpleBinding

@SuppressLint("SetTextI18n")
class Simple : Fragment() {

    private lateinit var binding: SimpleBinding
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SimpleBinding.inflate(inflater, container, false)

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
                val interestEquation = "I = Prt\n  ="
                val futureEquation = "\n\nF = P + I\n   ="
                val principal = "%,d".format(binding.edtPrincipal.text.toString().toInt())
                val rate = binding.edtRate.text.toString().toDouble() / 100
                val time = binding.edtTime.text.toString().toDouble() / 12
                val timeStr = roundToIntNot("%.3f".format(time).toDouble())
                val timeRate = "%.3f".format(rate * time)
                val interestInt = binding.edtPrincipal.text.toString().toDouble() * (rate * time)
                val interestStr = roundToIntNotString(interestInt)
                val future = roundToIntNotString(binding.edtPrincipal.text.toString().toDouble() + interestInt)
                binding.tvResult.text = "$interestEquation $principal (${roundToIntNot(
                    "%.4f".format(rate).toDouble())} × $timeStr)\n  = $principal × " +
                        "${roundToIntNot(timeRate.toDouble())}\n  = $interestStr$futureEquation $principal + $interestStr" +
                        "\n   = $future"
                binding.edtPrincipal.setText("")
                binding.edtPrincipal.requestFocus()
                binding.edtRate.setText("")
                binding.edtTime.setText("")
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
        if (view != null) imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun prompt(message: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.TOP or Gravity.CENTER, 0, 470)
        toast?.show()
    }
}