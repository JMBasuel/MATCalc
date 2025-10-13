package com.zinterr.matcalc

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zinterr.matcalc.databinding.DispersionBinding
import kotlin.math.*

@SuppressLint("SetTextI18n")
class Dispersion : Fragment() {

    private lateinit var binding: DispersionBinding
    private val dataResult = mutableListOf<Int>()
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DispersionBinding.inflate(inflater, container, false)

        binding.btnAdd.setOnClickListener {
            add()
        }

        binding.btnRemove.setOnClickListener {
            remove()
        }

        binding.btnCalculate.setOnClickListener {
            calculate()
        }

        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.btnDispersionBack.setOnClickListener {
            visible()
            reset()
        }

        return binding.root
    }

    private fun add() {
        val data = binding.edtData.text.toString()
        if (data != "") {
            binding.tvDataResult.text = "${binding.tvDataResult.text} $data".trim()
            dataResult.add(data.toInt())
            binding.edtData.setText("")
        } else prompt("Please insert value!")
    }

    private fun remove() {
        if (binding.tvDataResult.text != "") {
            val text = binding.tvDataResult.text
            val dispersionLastAdded = dataResult[dataResult.size - 1].toString()
            binding.tvDataResult.text =
                text.substring(0, text.length - dispersionLastAdded.length).trim()
            dataResult.removeAt(dataResult.size - 1)
        } else prompt("Nothing to remove!")
    }

    private fun calculate() {
        if (dataResult.size > 2) {
            dataResult.sort()
            val dispersionSum = dataResult.sum()
            val dispersionMean = dispersionSum.toDouble() / dataResult.size.toDouble()
            var dispersionSquareSum = 0.0
            for (i in 0 until dataResult.size) {
                dispersionSquareSum += (dataResult[i].toDouble() - dispersionMean).pow(2)
                val dispersionDiff = "${dataResult[i]} - ${
                    roundToIntNot("%.2f".format(dispersionMean).toDouble())
                } = ${roundToIntNot("%.2f".format(dataResult[i] - dispersionMean).toDouble())}"
                dataTable("${dataResult[i]}", dispersionDiff, "${
                        roundToIntNot("%.4f".format((dataResult[i] - dispersionMean).pow(2)).toDouble())}")
            }
            dataTable("", "Σ(x-x̄)²", roundToIntNotString(dispersionSquareSum))
            binding.tvResult.text = "Mean = ${roundToIntNot("%.2f".format(dispersionMean).toDouble())
            }   Range = ${dataResult[dataResult.size - 1] - dataResult[0]}"
            binding.tvDeviation.text =
                "VARIANCE:\ns² = Σ(x-x̄)²/n-1\n     = ${"%,.2f".format(dispersionSquareSum)} " +
                        "/ ${dataResult.size}-1\n     = ${"%.2f".format(dispersionSquareSum / 
                                (dataResult.size - 1))}\nSTANDARD DEVIATION:\ns = √s²\n    = " +
                        "√${"%.2f".format(dispersionSquareSum / (dataResult.size - 1))}\n    =" +
                        " ${"%.2f".format(sqrt(dispersionSquareSum / (dataResult.size - 1)))}"
            hideKeyboard()
            gone()
            dataResult.clear()
        } else prompt("Invalid data!")
    }

    private fun dataTable(cell1: String, cell2: String, cell3: String) {
        val trDispersionData = TableRow(requireContext())
        trDispersionData.addView(tableCell(cell1))
        trDispersionData.addView(tableCell(cell2))
        trDispersionData.addView(tableCell(cell3))
        binding.tbDispersion.addView(trDispersionData)
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

    private fun roundToIntNot(x: Double): Any {
        return if (x % 1.0 == 0.0) x.toInt()
        else x
    }

    private fun prompt(message: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.TOP or Gravity.CENTER, 0, 470)
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
        binding.tvDataResult.visibility = View.VISIBLE
        binding.loData.visibility = View.VISIBLE
        binding.loButtons.visibility = View.VISIBLE
        binding.btnCalculate.visibility = View.VISIBLE
        binding.btnBack.visibility = View.VISIBLE
        binding.tvResult.visibility = View.GONE
        binding.tvResult.text = ""
        binding.svScroll.visibility = View.GONE
        binding.tvDeviation.visibility = View.GONE
        binding.tvDeviation.text = ""
        binding.btnDispersionBack.visibility = View.GONE
    }

    private fun gone() {
        binding.tvDataResult.visibility = View.GONE
        binding.tvDataResult.text = ""
        binding.loData.visibility = View.GONE
        binding.loButtons.visibility = View.GONE
        binding.btnCalculate.visibility = View.GONE
        binding.btnBack.visibility = View.GONE
        binding.tvResult.visibility = View.VISIBLE
        binding.svScroll.visibility = View.VISIBLE
        binding.tvDeviation.visibility = View.VISIBLE
        binding.btnDispersionBack.visibility = View.VISIBLE
    }

    private fun reset() {
        for (i in binding.tbDispersion.childCount - 1 downTo 1) {
            val child = binding.tbDispersion.getChildAt(i)
            if (child is TableRow) binding.tbDispersion.removeViewAt(i)
        }
    }
}