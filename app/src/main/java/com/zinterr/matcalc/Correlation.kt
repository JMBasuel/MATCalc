package com.zinterr.matcalc

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.*
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zinterr.matcalc.databinding.CorrelationBinding
import kotlin.math.*

@SuppressLint("SetTextI18n")
class Correlation : Fragment() {

    private lateinit var binding: CorrelationBinding
    private lateinit var mainTableRow: TableRow
    private lateinit var sX: TextView
    private lateinit var sY: TextView
    private lateinit var sX2: TextView
    private lateinit var sY2: TextView
    private lateinit var sXY: TextView
    private var oldSX = 0
    private var newSX = 0
    private var oldSY = 0
    private var newSY = 0
    private var oldSX2 = 0
    private var newSX2 = 0
    private var oldSY2 = 0
    private var newSY2 = 0
    private var oldSXY = 0
    private var newSXY = 0
    private val xList = mutableListOf<Int>()
    private val yList = mutableListOf<Int>()
    private val x2List = mutableListOf<Int>()
    private val y2List = mutableListOf<Int>()
    private val xYList = mutableListOf<Int>()
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CorrelationBinding.inflate(inflater, container, false)

        mainTableRow =
            binding.tbCorrelation.getChildAt(binding.tbCorrelation.childCount - 2) as TableRow
        sX = binding.trTotal.getChildAt(0) as TextView
        sY = binding.trTotal.getChildAt(1) as TextView
        sX2 = binding.trTotal.getChildAt(2) as TextView
        sY2 = binding.trTotal.getChildAt(3) as TextView
        sXY = binding.trTotal.getChildAt(4) as TextView
        addWatcher()

        binding.btnAddRow.setOnClickListener {
            addRow()
        }
        binding.btnRemoveRow.setOnClickListener {
            removeRow()
        }
        binding.btnCalculate.setOnClickListener {
            calculate()
        }
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.btnReset.setOnClickListener {
            resetTable()
            visible()
            (mainTableRow.getChildAt(0) as EditText).requestFocus()
            addWatcher()
        }

        return binding.root
    }

    private fun addRow() {
        binding.btnRemoveRow.isEnabled = true
        val editTextX = mainTableRow.getChildAt(0) as? EditText
        editTextX?.let {
            val textX = it.text.toString()
            val textViewX = tableCell(textX) as TextView
            textViewX.textSize = 16f
            mainTableRow.removeViewAt(0)
            mainTableRow.addView(textViewX, 0)
        }
        val editTextY = mainTableRow.getChildAt(1) as? EditText
        editTextY?.let {
            val textY = it.text.toString()
            val textViewY = tableCell(textY) as TextView
            textViewY.textSize = 16f
            mainTableRow.removeViewAt(1)
            mainTableRow.addView(textViewY, 1)
        }
        xList.add(newSX)
        yList.add(newSY)
        x2List.add(newSX2)
        y2List.add(newSY2)
        xYList.add(newSXY)
        oldSX += newSX
        oldSY += newSY
        oldSX2 += newSX2
        oldSY2 += newSY2
        oldSXY += newSXY
        binding.tbCorrelation.removeView(binding.trTotal)
        tableRow()
        binding.tbCorrelation.addView(binding.trTotal)
        mainTableRow = binding.tbCorrelation.getChildAt(binding.tbCorrelation.childCount - 2) as TableRow
        binding.btnAddRow.isEnabled = false
        binding.btnCalculate.isEnabled = false
        addWatcher()
        (mainTableRow.getChildAt(0) as EditText).requestFocus()
    }

    private fun removeRow() {
        binding.btnAddRow.isEnabled = false
        binding.btnCalculate.isEnabled = false
        xList.removeAt(xList.lastIndex)
        yList.removeAt(yList.lastIndex)
        x2List.removeAt(x2List.lastIndex)
        y2List.removeAt(y2List.lastIndex)
        xYList.removeAt(xYList.lastIndex)
        oldSX = xList.sum()
        oldSY = yList.sum()
        oldSX2 = x2List.sum()
        oldSY2 = y2List.sum()
        oldSXY = xYList.sum()
        binding.tbCorrelation.removeViewAt(binding.tbCorrelation.childCount - 2)
        binding.tbCorrelation.removeViewAt(binding.tbCorrelation.childCount - 2)
        binding.tbCorrelation.removeView(binding.trTotal)
        tableRow()
        binding.tbCorrelation.addView(binding.trTotal)
        sX.text = "Σx="
        sY.text = "Σy="
        sX2.text = "Σx²="
        sY2.text = "Σy²="
        sXY.text = "Σxy="
        mainTableRow = binding.tbCorrelation.getChildAt(binding.tbCorrelation.childCount - 2) as TableRow
        addWatcher()
        binding.btnRemoveRow.isEnabled = binding.tbCorrelation.childCount > 3
        (mainTableRow.getChildAt(0) as EditText).requestFocus()
    }

    private fun calculate() {
        if (binding.tbCorrelation.childCount > 3) {
            hideKeyboard()
            val editTextX = mainTableRow.getChildAt(0) as? EditText
            editTextX?.let {
                val textX = it.text.toString()
                val textViewX = tableCell(textX) as TextView
                textViewX.textSize = 16f
                mainTableRow.removeViewAt(0)
                mainTableRow.addView(textViewX, 0)
            }
            val editTextY = mainTableRow.getChildAt(1) as? EditText
            editTextY?.let {
                val textY = it.text.toString()
                val textViewY = tableCell(textY) as TextView
                textViewY.textSize = 16f
                mainTableRow.removeViewAt(1)
                mainTableRow.addView(textViewY, 1)
            }
            xList.add(newSX)
            yList.add(newSY)
            x2List.add(newSX2)
            y2List.add(newSY2)
            xYList.add(newSXY)
            val correlationN = (binding.tbCorrelation.childCount - 2).toDouble()
            val correlationX = xList.sum().toDouble()
            val correlationY = yList.sum().toDouble()
            val correlationX2 = x2List.sum().toDouble()
            val correlationY2 = y2List.sum().toDouble()
            val correlationXY = xYList.sum().toDouble()
            val correlation = "%.2f".format(((correlationN * correlationXY) -
                    (correlationX * correlationY)) / sqrt(((correlationN * correlationX2) -
                    (correlationX.pow(2))) * ((correlationN * correlationY2) -
                    (correlationY.pow(2)))))
            if (correlation.toDouble() in -1.0..1.0) {
                val correlationPositive = if (correlation.toDouble() < 0) "NEGATIVE" else "POSITIVE"
                val correlationLoHi = when (correlation.toDouble().absoluteValue) {
                    1.0 -> "PERFECT"
                    in 0.91..0.99 -> "VERY HIGH"
                    in 0.71..0.90 -> "HIGH"
                    in 0.51..0.70 -> "MODERATELY"
                    in 0.31..0.50 -> "LOW"
                    in 0.01..0.30 -> "VERY LOW"
                    else -> 0
                }
                val correlationConclude = if (correlation.toDouble() != 0.0) "There is a $correlationLoHi " +
                        "$correlationPositive correlation between the two variables"
                else "There is NO correlation between the two variables"
                binding.tvResult.text = "r = nΣxy-ΣxΣy/√[nΣx²-(Σx)²][nΣy²-(Σy)²]\n   = " +
                        "[(${correlationN.toInt()}*${correlationXY.toInt()})-" +
                            "(${correlationX.toInt()}*${correlationY.toInt()})]/√[(" +
                        "${correlationN.toInt()}*${correlationX2.toInt()})-" +
                            "${correlationX.toInt()}²]\n       [(${correlationN.toInt()}" +
                        "*${correlationY2.toInt()})-${correlationY.toInt()}²]\n   = ${
                                roundToIntNot(correlation.toDouble())}\n$correlationConclude"
                gone()
            } else {
                prompt()
                resetTable()
                visible()
                addWatcher()
            }
        } else prompt()
    }

    private fun dataText(x: Int, y: Int) {
        val correlationX2 = mainTableRow.getChildAt(2) as? TextView
        val correlationY2 = mainTableRow.getChildAt(3) as? TextView
        val correlationXY = mainTableRow.getChildAt(4) as? TextView
        newSX = x
        newSY = y
        newSX2 = x * x
        newSY2 = y * y
        newSXY = x * y
        correlationX2?.text = "${x * x}"
        correlationY2?.text = "${y * y}"
        correlationXY?.text = "${x * y}"
        sX.text = "Σx=${oldSX + x}"
        sY.text = "Σy=${oldSY + y}"
        sX2.text = "Σx²=${oldSX2 + (x * x)}"
        sY2.text = "Σy²=${oldSY2 + (y * y)}"
        sXY.text = "Σxy=${oldSXY + (x * y)}"
    }

    private fun edtWatcher(): TextWatcher {
        val edtWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mainTableRow.let {
                    val edtXText = (it.getChildAt(0) as TextView).text.toString()
                    val edtYText = (it.getChildAt(1) as TextView).text.toString()
                    if (edtXText.isNotEmpty() && edtYText.isNotEmpty())
                        dataText(edtXText.toInt(), edtYText.toInt())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        return edtWatcher
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

    private fun areNotEmpty(tableRow: TableRow): Boolean {
        for (i in 0 until tableRow.childCount) {
            val view = tableRow.getChildAt(i)
            if (view is EditText && view.text.toString().trim().isEmpty()) return false
            else if (view is TextView && view.text.toString().trim().isEmpty()) return false
        }
        return true
    }

    private fun addWatcher() {
        for (i in 0..4) {
            val view = mainTableRow.getChildAt(i)
            if (view is EditText) {
                view.addTextChangedListener(edtWatcher())
                view.addTextChangedListener(rowWatcher())
            } else if (view is TextView) view.addTextChangedListener(rowWatcher())
        }
    }

    private fun tableRow() {
        val tableRow = TableRow(requireContext())
        tableRow.addView(editTableCell("x"))
        tableRow.addView(editTableCell("y"))
        tableRow.addView(tableCell(""))
        tableRow.addView(tableCell(""))
        tableRow.addView(tableCell(""))
        binding.tbCorrelation.addView(tableRow)
    }

    private fun resetTable() {
        binding.btnRemoveRow.isEnabled = false
        binding.btnAddRow.isEnabled = false
        binding.btnCalculate.isEnabled = false
        sX.text = ""
        sY.text = ""
        sX2.text = ""
        sY2.text = ""
        sXY.text = ""
        for (i in binding.tbCorrelation.childCount - 2 downTo 1)
            binding.tbCorrelation.removeViewAt(i)
        binding.tbCorrelation.removeView(binding.trTotal)
        tableRow()
        binding.tbCorrelation.addView(binding.trTotal)
        oldSX = 0
        oldSY = 0
        oldSX2 = 0
        oldSY2 = 0
        oldSXY = 0
        xList.clear()
        yList.clear()
        x2List.clear()
        y2List.clear()
        xYList.clear()
    }

    private fun roundToIntNot(x: Double): Any {
        return if (x % 1.0 == 0.0) x.toInt()
        else x
    }

    private fun prompt() {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), "Invalid data!", Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.TOP or Gravity.CENTER, 0, 400)
        toast?.show()
    }

    private fun editTableCell(hint: String): View {
        val tableCell = EditText(requireContext())
        tableCell.inputType = InputType.TYPE_CLASS_NUMBER
        tableCell.gravity = Gravity.CENTER
        tableCell.isEnabled = true
        tableCell.hint = hint
        tableCell.requestFocus()
        tableCell.height = 105
        return tableCell
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

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun visible() {
        binding.loButtons.visibility = View.VISIBLE
        binding.btnCalculate.visibility = View.VISIBLE
        binding.btnBack.visibility = View.VISIBLE
        binding.tvResult.visibility = View.GONE
        binding.tvResult.text = ""
        binding.btnReset.visibility = View.GONE
        mainTableRow = binding.tbCorrelation.getChildAt(binding.tbCorrelation.childCount - 2) as TableRow
    }

    private fun gone() {
        binding.loButtons.visibility = View.GONE
        binding.btnCalculate.visibility = View.GONE
        binding.btnBack.visibility = View.GONE
        binding.tvResult.visibility = View.VISIBLE
        binding.btnReset.visibility = View.VISIBLE
    }
}