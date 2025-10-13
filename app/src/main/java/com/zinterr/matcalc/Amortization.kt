package com.zinterr.matcalc

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zinterr.matcalc.databinding.AmortizationBinding
import kotlin.math.pow

@SuppressLint("SetTextI18n")
class Amortization : Fragment() {

    private lateinit var binding: AmortizationBinding
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AmortizationBinding.inflate(inflater, container, false)

        binding.btnCalculate.setOnClickListener {
            calculate()
        }

        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.btnAmortizationBack.setOnClickListener {
            visible()
            reset()
        }

        return binding.root
    }

    private fun calculate() {
        if (binding.edtPrincipal.text.toString() != "" && binding.edtRate.text.toString() != "" &&
            binding.edtTime.text.toString() != "") {
            if (binding.edtRate.text.toString().toDouble() <= 100) {
                var principal = binding.edtPrincipal.text.toString().toDouble()
                val rate = when (binding.spnCompoundFrequency.selectedItem.toString()) {
                    "Monthly" -> (binding.edtRate.text.toString().toDouble() / 100) / 12
                    "Annually" -> binding.edtRate.text.toString().toDouble() / 100
                    else -> 0.0
                }
                val time = when (binding.spnPayFrequency.selectedItem.toString()) {
                    "Monthly" -> binding.edtTime.text.toString().toInt() * 12
                    "Annually" -> binding.edtTime.text.toString().toInt()
                    else -> 0
                }
                val payment = (principal * rate) / (1 - ((1 + rate).pow(-time)))
                var totalPrincipal = 0.0
                var totalInterest = 0.0
                for (i in 1..time) {
                    val interest = principal * rate
                    val repayment = payment - interest
                    val balance = principal - repayment
                    totalPrincipal += repayment
                    totalInterest += interest
                    tableData(
                        i.toString(),
                        roundToIntNotString("%.2f".format(payment).toDouble()),
                        roundToIntNotString("%.2f".format(repayment).toDouble()),
                        roundToIntNotString("%.2f".format(interest).toDouble()),
                        roundToIntNotString("%.2f".format(balance).toDouble()))
                    principal = balance
                }
                val totalPayment = payment * time
                tableData(
                    "T",
                    roundToIntNotString("%.2f".format(totalPayment).toDouble()),
                    roundToIntNotString("%.2f".format(totalPrincipal).toDouble()),
                    roundToIntNotString("%.2f".format(totalInterest).toDouble()),
                    "")
                hideKeyboard()
                gone()
            } else prompt("Invalid rate!")
        } else prompt("Missing data!")
    }

    private fun tableData(cell1: String, cell2: String,
        cell3: String, cell4: String, cell5: String
    ) {
        val trAmortizationData = TableRow(requireContext())
        trAmortizationData.addView(tableCell(cell1))
        trAmortizationData.addView(tableCell(cell2))
        trAmortizationData.addView(tableCell(cell3))
        trAmortizationData.addView(tableCell(cell4))
        trAmortizationData.addView(tableCell(cell5))
        binding.tbAmortization.addView(trAmortizationData)
    }

    private fun tableCell(text: String): View {
        val textView = TextView(requireContext())
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        textView.gravity = Gravity.CENTER
        textView.textSize = 16f
        textView.text = text
        textView.height = 80
        return textView
    }

    private fun roundToIntNotString(x: Double): String {
        return if (x % 1.0 == 0.0) "%,d".format(x.toInt())
        else "%,.2f".format(x)
    }

    private fun prompt(message: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.TOP or Gravity.CENTER, 0, 150)
        toast?.show()
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun visible() {
        binding.loPrincipal.visibility = View.VISIBLE
        binding.edtPrincipal.requestFocus()
        binding.loRate.visibility = View.VISIBLE
        binding.loTime.visibility = View.VISIBLE
        binding.loCompoundFrequency.visibility = View.VISIBLE
        binding.loPayFrequency.visibility = View.VISIBLE
        binding.btnCalculate.visibility = View.VISIBLE
        binding.btnBack.visibility = View.VISIBLE
        binding.svScroll.visibility = View.GONE
        binding.btnAmortizationBack.visibility = View.GONE
    }

    private fun gone() {
        binding.loPrincipal.visibility = View.GONE
        binding.edtPrincipal.setText("")
        binding.loRate.visibility = View.GONE
        binding.edtRate.setText("")
        binding.loTime.visibility = View.GONE
        binding.edtTime.setText("")
        binding.loCompoundFrequency.visibility = View.GONE
        binding.spnCompoundFrequency.setSelection(0)
        binding.loPayFrequency.visibility = View.GONE
        binding.spnPayFrequency.setSelection(0)
        binding.btnCalculate.visibility = View.GONE
        binding.btnBack.visibility = View.GONE
        binding.svScroll.visibility = View.VISIBLE
        binding.btnAmortizationBack.visibility = View.VISIBLE
    }

    private fun reset() {
        for (i in binding.tbAmortization.childCount - 1 downTo 1) {
            val child = binding.tbAmortization.getChildAt(i)
            if (child is TableRow) binding.tbAmortization.removeViewAt(i)
        }
    }
}