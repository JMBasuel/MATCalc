package com.zinterr.matcalc

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.zinterr.matcalc.databinding.BasicBinding
import java.math.BigDecimal
import androidx.core.graphics.toColorInt

@SuppressLint("SetTextI18n")
class Basic : Fragment() {

    private lateinit var binding: BasicBinding
    private var canAddOperation = true
    private var canAddDecimal = true
    private var isEqualed = false
    private var calculations = ""
    private var final = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BasicBinding.inflate(inflater, container, false)

        binding.btnClear.setOnClickListener {
            clear()
        }

        binding.btnDelete.setOnClickListener {
            delete()
        }

        binding.btnPercent.setOnClickListener {
            percent()
        }

        binding.btnDivide.setOnClickListener {
            operation("÷")
        }

        binding.btnMultiply.setOnClickListener {
            operation("×")
        }

        binding.btnSubtract.setOnClickListener {
            operation("-")
        }

        binding.btnAdd.setOnClickListener {
            operation("+")
        }

        binding.btnDot.setOnClickListener {
            numButton(".")
        }

        binding.btnEqual.setOnClickListener {
            equal()
        }

        binding.btnOne.setOnClickListener {
            numButton("1")
        }

        binding.btnTwo.setOnClickListener {
            numButton("2")
        }

        binding.btnThree.setOnClickListener {
            numButton("3")
        }

        binding.btnFour.setOnClickListener {
            numButton("4")
        }

        binding.btnFive.setOnClickListener {
            numButton("5")
        }

        binding.btnSix.setOnClickListener {
            numButton("6")
        }

        binding.btnSeven.setOnClickListener {
            numButton("7")
        }

        binding.btnEight.setOnClickListener {
            numButton("8")
        }

        binding.btnNine.setOnClickListener {
            numButton("9")
        }

        binding.btnZero.setOnClickListener {
            numButton("0")
        }

        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return binding.root
    }

    private fun equal() {
        binding.tvResult.apply {
            if (binding.tvCalculation.text != "0") {
                final = calculate()
                text = "= $final"
                textSize = 48f
                setTextColor(Color.BLACK)
                binding.tvCalculation.apply {
                    textSize = 34f
                    setTextColor("#4C4C4C".toColorInt())
                }
                isEqualed = true
                canAddDecimal = true
                canAddOperation = true
            }
        }
    }

    private fun calculate(): String {
        val numOperator = numOperator()
        val timesDivision = timesDivision(numOperator)
        val result = addSubtract(timesDivision)
        return result.toString()
    }

    private fun addSubtract(listArg: MutableList<Any>): BigDecimal {
        var result = listArg[0] as BigDecimal
        for (i in listArg.indices) {
            if (listArg[i] is Char && i != listArg.lastIndex) {
                val operator = listArg[i]
                val next = listArg[i + 1] as BigDecimal
                if (operator == '+') result += next
                if (operator == '-') result -= next
            }
        }
        return result
    }

    private fun timesDivision(listArg: MutableList<Any>): MutableList<Any> {
        var list = listArg
        while (list.contains('×') || list.contains('÷')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(listArg: MutableList<Any>): MutableList<Any> {
        val list = mutableListOf<Any>()
        var restart = listArg.size
        for (i in listArg.indices) {
            if (listArg[i] is Char && i < restart) {
                val operator = listArg[i]
                val prev = listArg[i - 1] as BigDecimal
                if (i != listArg.lastIndex) {
                    val next = listArg[i + 1] as BigDecimal
                    when (operator) {
                        '×' -> {
                            list.add(prev * next)
                            restart = i + 1
                        }
                        '÷' -> {
                            list.add(prev / next)
                            restart = i + 1
                        }
                        else -> {
                            list.add(prev)
                            list.add(operator)
                        }
                    }
                } else list.add(prev)
            }
            if (i > restart) list.add(listArg[i])
        }
        return list
    }

    private fun numOperator(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var current = ""
        for (char in calculations) {
            if (char.isDigit() || char == '.') current += char
            else {
                list.add(current.toBigDecimal())
                current = ""
                list.add(char)
            }
        }
        if (current != "") list.add(current.toBigDecimal())
        return list
    }

    private fun numButton(num: String) {
        binding.tvCalculation.apply {
            textSize = textSize(length())
            if (isEqualed) {
                isEqualed = false
                binding.tvHistory.append("\n$text ${binding.tvResult.text}")
                reset(false)
            }
            if (num == "." && canAddDecimal) {
                if (!canAddOperation) {
                    append("0$num")
                    calculations += "0$num"
                } else {
                    append(num)
                    calculations += num
                }
                canAddDecimal = false
            } else if (num != ".") {
                if (text.toString() != "0") {
                    append(num)
                    calculations += num
                } else {
                    text = num
                    calculations = num
                }
                canAddOperation = true
            }
        }
        binding.tvResult.apply {
            text = "= ${calculate()}"
            visibility = View.VISIBLE
        }
    }

    private fun operation(op: String) {
        if (canAddOperation) binding.tvCalculation.apply {
            if (isEqualed) {
                isEqualed = false
                binding.tvHistory.append("\n$text ${binding.tvResult.text}")
                reset(false)
                text = "$final$op"
                calculations = "$final$op"
            } else {
                append(op)
                calculations += op
            }
            textSize = textSize(length())
            canAddOperation = false
            canAddDecimal = true
        }
        binding.tvResult.apply {
            text = "= ${calculate()}"
            visibility = View.VISIBLE
        }
    }

    private fun percent() {
        binding.tvCalculation.apply {
            if (canAddOperation && text.toString() != "0") {
                val numOperator = numOperator()
                val numOperatorPlain = numOperator()
                if (numOperator.last() is BigDecimal) {
                    numOperator[numOperator.lastIndex] = (numOperator.last() as BigDecimal).divide(
                        BigDecimal.valueOf(100))
                    numOperatorPlain[numOperatorPlain.lastIndex] = (numOperatorPlain.last() as BigDecimal).divide(
                        BigDecimal.valueOf(100)).toPlainString()
                }
                text = numOperator.joinToString("")
                calculations = numOperatorPlain.joinToString("")
                binding.tvResult.text = "= ${calculate()}"
            }
        }
    }

    private fun clear() {
        if (binding.tvCalculation.text.toString() != "0") reset(true)
        else binding.tvHistory.text = null
        isEqualed = false
        canAddDecimal = true
        canAddOperation = true
        final = ""
    }

    private fun delete() {
        binding.tvCalculation.apply {
            if (length() > 1 && !isEqualed) {
                if (!text.last().isDigit() && text.last() != '.') canAddOperation = true
                if (text.last() == '.') canAddDecimal = true
                calculations = calculations.dropLast(1)
                val numOperator = numOperator()
                if (numOperator.last() is BigDecimal)
                    numOperator[numOperator.lastIndex] =
                        (numOperator.last() as BigDecimal).stripTrailingZeros()
                text = numOperator.joinToString("")
                calculations = numOperator.joinToString("")
                if (!text.last().isDigit() && text.last() != '.') canAddOperation = false
                if (text.last() == '.') canAddDecimal = false
                textSize = textSize(length())
                binding.tvResult.text = "= ${calculate()}"
            } else {
                isEqualed = false
                canAddDecimal = true
                canAddOperation = true
                final = ""
                reset(true)
            }
        }
    }

    private fun textSize(len: Int): Float {
        return when (len) {
            in 1..12 -> 48f
            in 13..16 -> 40f
            else -> 34f
        }
    }

    private fun reset(hide: Boolean) {
        binding.tvCalculation.apply {
            text = null
            text = "0"
            calculations = "0"
            textSize = 48f
            setTextColor(Color.BLACK)
        }
        binding.tvResult.apply {
            text = null
            if (hide) visibility = View.GONE
            textSize = 34f
            setTextColor("#4C4C4C".toColorInt())
        }
    }
}