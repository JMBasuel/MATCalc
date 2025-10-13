package com.zinterr.matcalc

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.zinterr.matcalc.databinding.FibonacciBinding
import java.math.BigInteger

@SuppressLint("SetTextI18n")
class Fibonacci : Fragment() {

    private lateinit var binding: FibonacciBinding
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FibonacciBinding.inflate(inflater, container, false)

        binding.btnCalculate.setOnClickListener {
            if (binding.edtTerm.text.toString() != "") {
                val term = binding.edtTerm.text.toString().toInt()
                hideKeyboard()
                fibonacci(term)
            } else prompt()
        }

        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return binding.root
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun fibonacciCalc(term: Int): BigInteger {
        var a = BigInteger.ZERO
        var b = BigInteger.ONE
        repeat(term) {
            val temp = a
            a = b
            b += temp
        }
        return a
    }

    private fun fibonacci(term: Int) {
        val termVal = fibonacciCalc(term)
        binding.tvResult.text = "%,d".format(termVal)
        binding.edtTerm.setText("")
        binding.edtTerm.clearFocus()
    }

    private fun prompt() {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), "Please enter term number!", Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.TOP or Gravity.CENTER, 0, 150)
        toast?.show()
    }
}