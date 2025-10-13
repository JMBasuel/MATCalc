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
import com.zinterr.matcalc.databinding.RegressionBinding
import kotlin.math.*

@SuppressLint("SetTextI18n")
class Regression : Fragment() {

    private lateinit var binding: RegressionBinding
    private lateinit var mainTableRow: TableRow
    private var oldSX = 0
    private var newSX = 0
    private var oldSY = 0
    private var newSY = 0
    private var oldSXY = 0
    private var newSXY = 0
    private var oldSX2 = 0
    private var newSX2 = 0
    private var b = ""
    private var a = ""
    private val xList = mutableListOf<Int>()
    private val yList = mutableListOf<Int>()
    private val xYList = mutableListOf<Int>()
    private val x2List = mutableListOf<Int>()
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegressionBinding.inflate(inflater, container, false)

        mainTableRow = binding.tbRegression.getChildAt(binding.tbRegression.childCount - 2) as TableRow
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

        binding.btnTest.setOnClickListener {
            test()
        }

        binding.btnRegressionBack.setOnClickListener {
            resetTable()
            visible()
            ((binding.tbRegression.getChildAt(1) as TableRow).getChildAt(0) as EditText).requestFocus()
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
        xYList.add(newSXY)
        x2List.add(newSX2)
        oldSX += newSX
        oldSY += newSY
        oldSXY += newSXY
        oldSX2 += newSX2
        binding.tbRegression.removeView(binding.trTotal)
        tableRow()
        binding.tbRegression.addView(binding.trTotal)
        mainTableRow = binding.tbRegression.getChildAt(binding.tbRegression.childCount - 2) as TableRow
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
        xYList.removeAt(xYList.lastIndex)
        x2List.removeAt(x2List.lastIndex)
        oldSX = xList.sum()
        oldSY = yList.sum()
        oldSXY = xYList.sum()
        oldSX2 = x2List.sum()
        binding.tbRegression.removeViewAt(binding.tbRegression.childCount - 2)
        binding.tbRegression.removeViewAt(binding.tbRegression.childCount - 2)
        binding.tbRegression.removeView(binding.trTotal)
        tableRow()
        binding.tbRegression.addView(binding.trTotal)
        binding.tcSX.text = "Σx="
        binding.tcSY.text = "Σy="
        binding.tcSXY.text = "Σxy="
        binding.tcSX2.text = "Σx²="
        mainTableRow = binding.tbRegression.getChildAt(binding.tbRegression.childCount - 2) as TableRow
        addWatcher()
        binding.btnRemoveRow.isEnabled = binding.tbRegression.childCount > 3
        (mainTableRow.getChildAt(0) as EditText).requestFocus()
    }

    private fun calculate() {
        if (binding.tbRegression.childCount > 3) {
            hideKeyboard()
            xList.add(newSX)
            yList.add(newSY)
            x2List.add(newSX2)
            xYList.add(newSXY)
            val regressionN = (binding.tbRegression.childCount - 2).toDouble()
            val regressionX = xList.sum().toDouble()
            val regressionY = yList.sum().toDouble()
            val regressionXY = xYList.sum().toDouble()
            val regressionX2 = x2List.sum().toDouble()
            b = roundToIntNot("%.2f".format(((regressionN * regressionXY) - (regressionX * regressionY)) /
                    ((regressionN * regressionX2) - (regressionX.pow(2)))).toDouble()).toString()
            a = roundToIntNot("%.2f".format((regressionY - (b.toDouble() * regressionX)) /
                    regressionN).toDouble()).toString()
            binding.tvResult.text = "b = nΣxy-ΣxΣy/nΣx²-(Σx)²\n   = ${regressionN.toInt()}" +
                    "(${regressionXY.toInt()})-${regressionX.toInt()}(${regressionY.toInt()})/" +
                    "${regressionN.toInt()}(${regressionX2.toInt()})-${regressionX.toInt()}²\n   " +
                    "= $b\na = Σy - b(Σx)/n\n   = ${regressionY.toInt()} - $b(${regressionX.toInt()})" +
                    "/${regressionN.toInt()}\n   = $a\ny = a + bx\n   = $a + ${b}x"
            binding.tvTestResult.text = "$a + ${b}x = "
            gone()
        } else prompt("Invalid data!", 500)
    }

    private fun test() {
        if (binding.edtTest.text.toString() != "") {
            binding.tvTestResult.text = "$a + ${b}(${binding.edtTest.text}) = ${
                (a.toDouble() + (b.toDouble() * binding.edtTest.text.toString().toDouble())).roundToInt()}"
            hideKeyboard()
            binding.edtTest.setText("")
            binding.edtTest.clearFocus()
        } else prompt("Please enter a value!", 580)
    }

    private fun dataText(x: Int, y: Int) {
        val xY = mainTableRow.getChildAt(2) as? TextView
        val x2 = mainTableRow.getChildAt(3) as? TextView
        newSX = x
        newSY = y
        newSXY = x * y
        newSX2 = x * x
        xY?.text = "${x * y}"
        x2?.text = "${x * x}"
        binding.tcSX.text = "Σx=${oldSX + x}"
        binding.tcSY.text = "Σy=${oldSY + y}"
        binding.tcSXY.text = "Σxy=${oldSXY + (x * y)}"
        binding.tcSX2.text = "Σx²=${oldSX2 + (x * x)}"
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
        binding.tbRegression.addView(tableRow)
    }

    private fun resetTable() {
        binding.btnRemoveRow.isEnabled = false
        binding.btnAddRow.isEnabled = false
        binding.btnCalculate.isEnabled = false
        binding.tcSX.text = ""
        binding.tcSY.text = ""
        binding.tcSXY.text = ""
        binding.tcSX2.text = ""
        for (i in binding.tbRegression.childCount - 2 downTo 1) {
            binding.tbRegression.removeViewAt(i)
        }
        binding.tbRegression.removeView(binding.trTotal)
        tableRow()
        binding.tbRegression.addView(binding.trTotal)
        oldSX = 0
        oldSY = 0
        oldSXY = 0
        oldSX2 = 0
        xList.clear()
        yList.clear()
        xYList.clear()
        x2List.clear()
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

    private fun roundToIntNot(x: Double): Any {
        return if (x % 1.0 == 0.0) x.toInt()
        else x
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (view != null)
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun prompt(message: String, yOffset: Int) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.TOP or Gravity.CENTER, 0, yOffset)
        toast?.show()
    }

    private fun visible() {
        binding.svScroll.visibility = View.VISIBLE
        binding.loButtons.visibility = View.VISIBLE
        binding.btnCalculate.visibility = View.VISIBLE
        binding.btnBack.visibility = View.VISIBLE
        binding.tvResult.visibility = View.GONE
        binding.tvResult.text = ""
        binding.tvTestResult.visibility = View.GONE
        binding.tvTestResult.text = ""
        binding.loTest.visibility = View.GONE
        binding.btnTest.visibility = View.GONE
        binding.btnRegressionBack.visibility = View.GONE
        mainTableRow = binding.tbRegression.getChildAt(binding.tbRegression.childCount - 2) as TableRow
    }

    private fun gone() {
        binding.svScroll.visibility = View.GONE
        binding.loButtons.visibility = View.GONE
        binding.btnCalculate.visibility = View.GONE
        binding.btnBack.visibility = View.GONE
        binding.tvResult.visibility = View.VISIBLE
        binding.tvTestResult.visibility = View.VISIBLE
        binding.loTest.visibility = View.VISIBLE
        binding.btnTest.visibility = View.VISIBLE
        binding.btnRegressionBack.visibility = View.VISIBLE
    }
}