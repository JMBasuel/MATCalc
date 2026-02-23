package com.zinterr.matcalc

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.*
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zinterr.matcalc.databinding.TendencyBinding

@SuppressLint("SetTextI18n")
class Tendency : Fragment() {

    private lateinit var binding: TendencyBinding
    private lateinit var mainTableRow: TableRow
    private lateinit var freqTotal: TextView
    private lateinit var fxTotal: TextView
    private var classSize = 0
    private var interval = 0
    private var classInterval = ""
    private var oldFreq = 0
    private var newFreq = 0
    private var oldFx = 0.0
    private var newFx = 0.0
    private val classIntervalList = mutableListOf<String>()
    private val freqList = mutableListOf<Int>()
    private val boundList = mutableListOf<Double>()
    private val lessFreqList = mutableListOf<Int>()
    private val fxList = mutableListOf<Double>()
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TendencyBinding.inflate(inflater, container, false)

        mainTableRow = binding.tbCentral.getChildAt(binding.tbCentral.childCount - 2) as TableRow
        freqTotal = binding.trTotal.getChildAt(1) as TextView
        fxTotal = binding.trTotal.getChildAt(2) as TextView
        addWatcher()

        binding.btnAddRow.setOnClickListener {
            addRow()
        }

        binding.btnRemoveRow.setOnClickListener {
            removeRow()
        }

        binding.btnCalculate.setOnClickListener {
            if (binding.tbCentral.childCount > 4) {
                calculate()
                gone()
            } else prompt("Invalid data!")
        }

        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.btnReset.setOnClickListener {
            resetTable()
            visible()
            addWatcher()
        }

        return binding.root
    }

    private fun calculate() {
        freqList.add((mainTableRow.getChildAt(1) as TextView).text.toString().toInt())
        fxList.add((mainTableRow.getChildAt(5) as TextView).text.toString().toDouble())
        for (i in 1 until binding.tbCentral.childCount - 1) {
            classIntervalList.add(((binding.tbCentral.getChildAt(i) as TableRow)
                .getChildAt(0) as TextView).text.toString())
            boundList.add(((binding.tbCentral.getChildAt(i) as TableRow)
                .getChildAt(3) as TextView).text.toString().toDouble())
            lessFreqList.add(((binding.tbCentral.getChildAt(i) as TableRow)
                .getChildAt(4) as TextView).text.toString().toInt())
        }
        val meanEquation = "Mean = ΣfX/n\n           ="
        val centralMean = "%.2f".format(fxList.sum() / freqList.sum())
        val medianEquation = "Median = LCBmd + C(((n/2) - <CFb)/fmd)\n               ="
        val median = lessFreqList.indexOfFirst { it >= freqList.sum() / 2 }
        val centralMedian = "%.2f".format(boundList[median] + (classSize *
                (((freqList.sum() / 2) - lessFreqList[median - 1]).toDouble() / freqList[median].toDouble())))
        val modeEquation = "Mode = LCBmo + C((Fmo-Fb)/(2Fmo-Fb-Fa))\n           ="
        val mode = freqList.indexOf(freqList.max())
        val modalClass = "Modal class = Class ${classIntervalList[mode]}\n"
        val modeB = freqList.getOrNull(mode - 1) ?: 0
        val modeA = freqList.getOrNull(mode + 1) ?: 0
        val centralMode = "%.2f".format(boundList[mode] + (classSize *
                ((freqList[mode] - modeB).toDouble() / ((2 * freqList[mode]) - modeB - modeA).toDouble())))
        val medianClass = if ((lessFreqList.indexOf(lessFreqList[median]) + 1).toString().endsWith("3"))
            "${lessFreqList.indexOf(lessFreqList[median]) + 1}rd class"
        else if ((lessFreqList.indexOf(lessFreqList[median]) + 1).toString().endsWith("2"))
            "${lessFreqList.indexOf(lessFreqList[median]) + 1}nd class"
        else if ((lessFreqList.indexOf(lessFreqList[median]) + 1).toString().endsWith("1"))
            "${lessFreqList.indexOf(lessFreqList[median]) + 1}st class"
        else "${lessFreqList.indexOf(lessFreqList[median]) + 1}th class"
        binding.tvResult.text = "$meanEquation ${roundToIntNot(fxList.sum())}/${freqList.sum()}\n" +
                "           = $centralMean\nMedian class = ${freqList.sum()}/2 = ${freqList.sum() / 2} " +
                "⇒ ${freqList.sum() / 2} < ${lessFreqList[median]} ($medianClass)\n$medianEquation " +
                "${boundList[median]} + $classSize(((${freqList.sum()}/2) - ${lessFreqList[median - 1]}" +
                ")/${freqList[median]})\n               = $centralMedian\n$modalClass$modeEquation " +
                "${boundList[mode]} + $classSize((${freqList[mode]} - $modeB)/(2(${freqList[mode]}) - " +
                "$modeB - $modeA))\n           = $centralMode"
        val editTextFrequency = mainTableRow.getChildAt(1) as? EditText
        editTextFrequency?.let {
            val textFrequency = it.text.toString()
            val textViewFrequency = tableCell(textFrequency) as TextView
            textViewFrequency.textSize = 16f
            mainTableRow.removeViewAt(1)
            mainTableRow.addView(textViewFrequency, 1)
        }
        gone()
    }

    private fun removeRow() {
        binding.btnAddRow.isEnabled = false
        binding.btnCalculate.isEnabled = false
        freqList.removeAt(freqList.lastIndex)
        fxList.removeAt(fxList.lastIndex)
        oldFreq = freqList.sum()
        oldFx = fxList.sum()
        binding.tbCentral.removeViewAt(binding.tbCentral.childCount - 2)
        mainTableRow = binding.tbCentral.getChildAt(binding.tbCentral.childCount - 2) as TableRow
        classInterval = (mainTableRow.getChildAt(0) as TextView).text.toString()
        binding.tbCentral.removeViewAt(binding.tbCentral.childCount - 2)
        binding.tbCentral.removeView(binding.trTotal)
        if (binding.tbCentral.childCount < 2) topTableRow()
        else tableRow(classInterval)
        binding.tbCentral.addView(binding.trTotal)
        freqTotal.text = ""
        fxTotal.text = ""
        mainTableRow = binding.tbCentral.getChildAt(binding.tbCentral.childCount - 2) as TableRow
        addWatcher()
        binding.btnRemoveRow.isEnabled = binding.tbCentral.childCount > 3
    }

    private fun addRow() {
        binding.btnRemoveRow.isEnabled = true
        val editTextFrequency = mainTableRow.getChildAt(1) as? EditText
        editTextFrequency?.let {
            val textFrequency = it.text.toString()
            val textViewFrequency = tableCell(textFrequency) as TextView
            textViewFrequency.textSize = 16f
            mainTableRow.removeViewAt(1)
            mainTableRow.addView(textViewFrequency, 1)
        }
        freqList.add(newFreq)
        fxList.add(newFx)
        oldFreq += newFreq
        oldFx += newFx
        val editTextInterval = mainTableRow.getChildAt(0) as? EditText
        editTextInterval?.let {
            val textInterval = it.text.toString().substring((it.text.length - 1) / 2 + 1).toInt() + 1
            classInterval = "$textInterval-${textInterval + interval}"
            val textViewInterval = tableCell("${it.text.substring(0, (it.text.length - 1) / 2)
                }-${it.text.substring((it.text.length - 1) / 2 + 1)}") as TextView
            textViewInterval.textSize = 16f
            mainTableRow.removeViewAt(0)
            mainTableRow.addView(textViewInterval, 0)
        } ?: run {
            val textViewInterval = mainTableRow.getChildAt(0) as? TextView
            textViewInterval?.let {
                classInterval = "${it.text.substring((it.text.length - 1) / 2 + 1).toInt() + 1
                }-${it.text.substring((it.text.length - 1) / 2 + 1).toInt() + 1 + interval}"
            }
        }
        binding.tbCentral.removeView(binding.trTotal)
        tableRow(classInterval)
        binding.tbCentral.addView(binding.trTotal)
        mainTableRow = binding.tbCentral.getChildAt(binding.tbCentral.childCount - 2) as TableRow
        binding.btnAddRow.isEnabled = false
        binding.btnCalculate.isEnabled = false
        addWatcher()
    }

    private fun dataText(text: String, freq: Int) {
        val childCentralMark = mainTableRow.getChildAt(2) as? TextView
        val childLowerBound = mainTableRow.getChildAt(3) as? TextView
        val childCumulativeFreq = mainTableRow.getChildAt(4) as? TextView
        val childFX = mainTableRow.getChildAt(5) as? TextView
        val centralMark = (text.substring(0, (text.length - 1) / 2)
            .toDouble() + text.substring((text.length - 1) / 2 + 1).toDouble()) / 2.0
        newFreq = freq
        newFx = freq * centralMark
        childCentralMark?.text = "${roundToIntNot(centralMark)}"
        childLowerBound?.text = "${text.substring(0, (text.length - 1) / 2).toDouble() - 0.5}"
        childCumulativeFreq?.text = "${oldFreq + freq}"
        childFX?.text = "${roundToIntNot(freq * centralMark)}"
        freqTotal.text = "${oldFreq + freq}"
        fxTotal.text = "${roundToIntNot(oldFx + (freq * centralMark))}"
    }

    private fun edtWatcher(): TextWatcher {
        val edtWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mainTableRow.let {
                    val edtOne = it.getChildAt(0)
                    val edtTwo = it.getChildAt(1)
                    if (edtOne is EditText && edtTwo is EditText) {
                        val edtOneText = edtOne.text.toString()
                        val edtTwoText = edtTwo.text.toString()
                        edtTwo.isEnabled = edtOneText.length >= 3
                        if (count == 0 && before == 1) interval = 0
                        if (edtOneText.isNotEmpty() && edtTwoText.isNotEmpty()) {
                            if (edtOneText[(edtOneText.length - 1) / 2] == '-') {
                                if (edtOneText.matches(Regex("^[0-9-]+$"))) {
                                    interval = edtOneText.substring(((edtOneText.length - 1) / 2) + 1)
                                        .toInt() - edtOneText.substring(0, (edtOneText.length - 1) / 2).toInt()
                                    classSize = interval + 1
                                }
                            }
                            if (edtOneText[(edtOneText.length - 1) / 2] == '-' && interval > 0)
                                dataText(edtOneText, edtTwoText.toInt())
                            else {
                                edtOne.setText("")
                                edtOne.requestFocus()
                                edtTwo.setText("")
                                freqTotal.text = ""
                                fxTotal.text = ""
                                for (i in 0..5) {
                                    val child = (binding.tbCentral.getChildAt(1) as TableRow).getChildAt(i)
                                    if (child is EditText) child.setText("")
                                    else if (child is TextView) child.text = ""
                                }
                                prompt("Invalid class!")
                            }
                        }
                    } else if (edtOne is TextView && edtTwo is EditText) {
                        val edtOneText = edtOne.text.toString()
                        val edtTwoText = edtTwo.text.toString()
                        if (edtOneText.isNotEmpty() && edtTwoText.isNotEmpty())
                            dataText(edtOneText, edtTwoText.toInt())
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        return edtWatcher
    }

    private fun areNotEmpty(tableRow: TableRow): Boolean {
        for (i in 0 until tableRow.childCount) {
            val view = tableRow.getChildAt(i)
            if (view is EditText && view.text.toString().trim().isEmpty()) return false
            else if (view is TextView && view.text.toString().trim().isEmpty()) return false
        }
        return true
    }

    private fun rowWatcher(): TextWatcher {
        val rowWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mainTableRow.let {
                    binding.btnAddRow.isEnabled = areNotEmpty(it)
                    binding.btnCalculate.isEnabled = areNotEmpty(it)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        return rowWatcher
    }

    private fun addWatcher() {
        for (i in 0..5) {
            val view = mainTableRow.getChildAt(i)
            if (view is EditText) {
                view.addTextChangedListener(edtWatcher())
                view.addTextChangedListener(rowWatcher())
            } else if (view is TextView) view.addTextChangedListener(rowWatcher())
        }
    }

    private fun tableRow(classInterval: String) {
        val tableRow = TableRow(requireContext())
        tableRow.addView(tableCell(classInterval))
        tableRow.addView(editTableCell(true))
        tableRow.addView(tableCell(""))
        tableRow.addView(tableCell(""))
        tableRow.addView(tableCell(""))
        tableRow.addView(tableCell(""))
        binding.tbCentral.addView(tableRow)
    }

    private fun textEditTableCell(): View {
        val tableCell = EditText(requireContext())
        tableCell.inputType = InputType.TYPE_CLASS_TEXT
        tableCell.gravity = Gravity.CENTER
        tableCell.hint = "10-19"
        tableCell.requestFocus()
        tableCell.height = 105
        return tableCell
    }

    private fun topTableRow() {
        val tableRow = TableRow(requireContext())
        tableRow.addView(textEditTableCell())
        tableRow.addView(editTableCell(false))
        tableRow.addView(tableCell(""))
        tableRow.addView(tableCell(""))
        tableRow.addView(tableCell(""))
        tableRow.addView(tableCell(""))
        binding.tbCentral.addView(tableRow)
    }

    private fun resetTable() {
        binding.btnRemoveRow.isEnabled = false
        binding.btnAddRow.isEnabled = false
        binding.btnCalculate.isEnabled = false
        classIntervalList.clear()
        freqList.clear()
        boundList.clear()
        lessFreqList.clear()
        fxList.clear()
        oldFreq = 0
        oldFx = 0.0
        freqTotal.text = ""
        fxTotal.text = ""
        for (i in binding.tbCentral.childCount - 2 downTo 1) {
            binding.tbCentral.removeViewAt(i)
        }
        binding.tbCentral.removeView(binding.trTotal)
        topTableRow()
        binding.tbCentral.addView(binding.trTotal)
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

    private fun editTableCell(enabled: Boolean): View {
        val tableCell = EditText(requireContext())
        tableCell.inputType = InputType.TYPE_CLASS_NUMBER
        tableCell.gravity = Gravity.CENTER
        tableCell.isEnabled = enabled
        tableCell.hint = "5"
        tableCell.requestFocus()
        tableCell.height = 105
        return tableCell
    }

    private fun prompt(message: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.TOP or Gravity.CENTER, 0, 400)
        toast?.show()
    }

    private fun roundToIntNot(x: Double): Any {
        return if (x % 1.0 == 0.0) x.toInt()
        else x
    }

    private fun visible() {
        binding.loButtons.visibility = View.VISIBLE
        binding.btnCalculate.visibility = View.VISIBLE
        binding.btnBack.visibility = View.VISIBLE
        binding.tvResult.visibility = View.GONE
        binding.tvResult.text = ""
        binding.btnReset.visibility = View.GONE
        mainTableRow = binding.tbCentral.getChildAt(binding.tbCentral.childCount - 2) as TableRow
    }

    private fun gone() {
        binding.loButtons.visibility = View.GONE
        binding.btnCalculate.visibility = View.GONE
        binding.btnBack.visibility = View.GONE
        binding.tvResult.visibility = View.VISIBLE
        binding.btnReset.visibility = View.VISIBLE
    }
}