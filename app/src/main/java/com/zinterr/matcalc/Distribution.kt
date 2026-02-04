package com.zinterr.matcalc

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zinterr.matcalc.databinding.DistributionBinding
import kotlin.math.*

@SuppressLint("SetTextI18n")
class Distribution : Fragment() {

    private lateinit var binding: DistributionBinding
    private val dataList = mutableListOf<Int>()
    private val classIntervals = mutableListOf<String>()
    private val frequencies = mutableListOf<Int>()
    private val lower = mutableListOf<Double>()
    private val upper = mutableListOf<Double>()
    private val mark = mutableListOf<Double>()
    private val lessList = mutableListOf<Int>()
    private val moreList = mutableListOf<Int>()
    private val relative = mutableListOf<Double>()
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DistributionBinding.inflate(inflater, container, false)

        binding.btnAdd.setOnClickListener {
            val data = binding.edtData.text.toString()
            if (data != "") add(data.toInt())
            else prompt("Please insert value!")
        }

        binding.btnRemove.setOnClickListener {
            if (binding.tvDataResult.text != "") remove()
            else prompt("Nothing to remove!")
        }

        binding.btnCalculate.setOnClickListener {
            if (dataList.size > 2) {
                hideKeyboard()
                binding.edtData.clearFocus()
                calculate()
                gone()
            } else prompt("Invalid data!")
        }

//        FOR UPDATE
        binding.btnToTend.setOnClickListener {
            send()
            findNavController().navigate(DistributionDirections.actionDistributionToTendency())
        }

//        FOR UPDATE
        binding.btnFDTBack.setOnClickListener {
            visible()
            reset()
        }

        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return binding.root
    }


    //    FOR UPDATE
    private fun send() {
        // SEND DATA TO CENTRAL TENDENCY
    }

    //    FOR UPDATE
    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun calculate() {
        dataList.sort()
        val dataCount = dataList.size.toDouble()
        val range = dataList.last() - dataList.first()
        val classes = (1 + (3.322 * log10(dataCount))).roundToInt()
        val classInterval = (range.toDouble() / classes.toDouble()).roundToInt()
        var interval = dataList.first()
        var less = 0
        var more = 0
        repeat(classes) {
            if (interval > dataList.last()) return@repeat
            else {
                val nextInterval = interval + classInterval
                val classText = "$interval-$nextInterval"
                classIntervals.add(classText)
                var frequency = 0
                for (j in interval..nextInterval) {
                    frequency += dataList.count { it == j }
                }
                frequencies.add(frequency)
                lower.add(interval - 0.5)
                upper.add(nextInterval + 0.5)
                mark.add(((interval - 0.5) + (nextInterval + 0.5)) / 2)
                relative.add((frequency.toDouble() / dataCount) * 100.0)
                interval = nextInterval + 1
            }
        }
        if (dataList.last() >= interval) {
            val nextInterval = interval + classInterval
            val classText = "$interval-$nextInterval"
            classIntervals.add(classText)
            var frequency = 0
            for (i in interval..nextInterval) {
                frequency += dataList.count { it == i }
            }
            frequencies.add(frequency)
            lower.add(interval - 0.5)
            upper.add(nextInterval + 0.5)
            mark.add(((interval - 0.5) + (nextInterval + 0.5)) / 2)
            relative.add((frequency.toDouble() / dataCount) * 100.0)
        }
        val frequenciesFlip = frequencies.reversed()
        for (i in 0 until frequencies.size) {
            less += frequencies[i]
            more += frequenciesFlip[i]
            lessList.add(less)
            moreList.add(more)
        }
        moreList.sortDescending()
        var relativeSum = 0.0
        for (i in relative) {
            relativeSum += i
        }
        binding.tbDistribution.removeView(binding.trTotal)
        table()
        binding.tbDistribution.addView(binding.trTotal)
        binding.tcFTotal.text = "${dataList.size}"
        binding.tcRfTotal.text = "~${relativeSum.roundToInt()}"
        binding.tvResult.text = "R = ${dataList.last()} - ${dataList.first()}\n  = $range\n" +
                "K = 1 + 3.322log${dataCount.toInt()}\n  = $classes\n" +
                "C = $range / $classes\n  = $classInterval"
    }

    private fun table() {
        for (i in 0 until frequencies.size) {
            val tableRow = TableRow(requireContext())
            tableRow.addView(tableCell(classIntervals[i]))
            tableRow.addView(tableCell(frequencies[i].toString()))
            tableRow.addView(tableCell(lower[i].toString()))
            tableRow.addView(tableCell(upper[i].toString()))
            tableRow.addView(tableCell(roundToIntNot(mark[i]).toString()))
            tableRow.addView(tableCell(lessList[i].toString()))
            tableRow.addView(tableCell(moreList[i].toString()))
            tableRow.addView(tableCell(roundToIntNot("%.2f".format(relative[i])
                .toDouble()).toString()))
            binding.tbDistribution.addView(tableRow)
        }
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

    private fun prompt(message: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.TOP or Gravity.CENTER, 0, 490)
        toast?.show()
    }

    private fun add(data: Int) {
        binding.tvDataResult.text = "${binding.tvDataResult.text} $data".trim()
        dataList.add(data)
        binding.tvDataCount.text = "${dataList.size}"
        binding.edtData.setText("")
    }

    private fun remove() {
        val text = binding.tvDataResult.text
        val last = dataList.last().toString()
        binding.tvDataResult.text = text.dropLast(last.length).trim()
        dataList.removeAt(dataList.lastIndex)
        binding.tvDataCount.text = "${dataList.size}"
    }

    private fun roundToIntNot(x: Double): Any {
        return if (x % 1.0 == 0.0) x.toInt()
        else x
    }

    private fun visible() {
        binding.tvDataResult.visibility = View.VISIBLE
        binding.tvDataResult.text = ""
        binding.parentRelative.visibility = View.VISIBLE
        binding.edtData.setText("")
        binding.deltaRelative.visibility = View.VISIBLE
        binding.btnCalculate.visibility = View.VISIBLE
        binding.btnBack.visibility = View.VISIBLE
        binding.tvDataCount.visibility = View.VISIBLE
        binding.tvResult.visibility = View.GONE
        binding.pathRelative.visibility = View.GONE
        binding.btnFDTBack.visibility = View.GONE
        classIntervals.clear()
        frequencies.clear()
        lower.clear()
        mark.clear()
        lessList.clear()
    }

    private fun gone() {
        binding.tvDataResult.visibility = View.GONE
        binding.parentRelative.visibility = View.GONE
        binding.deltaRelative.visibility = View.GONE
        binding.btnCalculate.visibility = View.GONE
        binding.btnBack.visibility = View.GONE
        binding.tvDataCount.visibility = View.GONE
        binding.tvResult.visibility = View.VISIBLE
        binding.pathRelative.visibility = View.VISIBLE
        binding.btnFDTBack.visibility = View.VISIBLE
        binding.tvDataCount.text = "0"
        dataList.clear()
        upper.clear()
        moreList.clear()
        relative.clear()
    }

    private fun reset() {
        for (i in binding.tbDistribution.childCount - 2 downTo 1) {
            val child = binding.tbDistribution.getChildAt(i)
            if (child is TableRow) binding.tbDistribution.removeViewAt(i)
        }
    }
}