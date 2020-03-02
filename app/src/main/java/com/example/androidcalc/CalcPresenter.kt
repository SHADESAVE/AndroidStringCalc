package com.example.androidcalc

import android.util.Log
import kotlin.math.expm1

class CalcPresenter {

    private var view: CalcView? = null
    private lateinit var expression: String
    private var currentSymb: Char? = null
    private var position: Int = -1

    fun bindView(view: CalcView) {
        this.view = view
    }

    fun unbindView() {
        this.view = null
    }

    fun onButtonClick(expression: String) {
        this.expression = expression
        this.position = -1
        val result = this.calculate()
        view?.calculate(result)
    }

    private fun calculate(): Double {
        nextSymbol()
        val result = stepZero()
        if (position < expression.length) {
            getException()
        }
        return result
    }

    private fun stepZero(): Double {
        val result = stepOne()
        while (true) {
            when {
                check('>') -> {
                    return if (check('=')) {
                        if (result >= stepOne()) getTrueResult() else getFalseResult()
                    } else {
                        if (result > stepOne()) getTrueResult() else getFalseResult()
                    }
                }

                check('<') -> {
                    return if (check('=')) {
                        if (result <= stepOne()) getTrueResult() else getFalseResult()
                    } else {
                        if (result < stepOne()) getTrueResult() else getFalseResult()
                    }
                }
                check('=') -> {
                    return if (check('=')) {
                        if (result == stepZero()) getTrueResult() else getFalseResult()
                    } else {
                        if (result == stepZero()) getTrueResult() else getFalseResult()
                    }
                }
                else -> return result
            }
        }
    }

    private fun stepOne(): Double {
        var result = stepTwo()
        while (true) {
            when {
                check('+') -> result += stepTwo()
                check('-') -> result -= stepTwo()
                else -> return result
            }
        }
    }

    private fun stepTwo(): Double {
        var result = nextNumber()
        while (true) {
            when {
                check('*') -> result *= nextNumber()
                check('/') -> result /= nextNumber()
                else -> return result
            }
        }
    }

    private fun nextNumber(): Double {
        if (check('+')) return nextNumber()
        if (check('-')) return -nextNumber()

        var result = 0.0
        val oldPosition = position

        if (check('(')) {
            result = stepZero() // Считаем результат в скобках
            check(')')
        } else {
            if ((currentSymb!! in '0'..'9') || currentSymb == '.') {
                while ((currentSymb!! in '0'..'9') || currentSymb == '.') nextSymbol()

                result = try {
                    expression.substring(oldPosition, position).toDouble()
                } catch (t: Throwable) {
                    Log.e("err", t.toString())
                    view?.exception(t.toString())
                    0.0
                }
            } else {
                getException()
            }
        }
        return result
    }

    private fun nextSymbol() {
        currentSymb =
            if (++position < expression.length)
                expression[position]
            else '!'
    }

    private fun check(newSymbol: Char): Boolean {
        if (currentSymb == newSymbol) {
            nextSymbol()
            return true
        }
        return false
    }

    private fun getException() {
        when (currentSymb) {
            '?', ':', '>', '<', '=' -> view?.exception("Проверьте правильность ввода тернарного выражения.\nПример: \"(10 - 2) * 0.5 >= 8 ? 1 : 4\"")
            ')' -> view?.exception("Отсутствует открывающая скобка: \"(\"")
            '!' -> view?.exception("Проверьте правильность ввода выражения.\nПример обычного: \"(10 - 2) * 0.5\".\nПример тернарного: \n\"(10 - 2) * 0.5 >= 8 ? 1 : 4\"")
            else -> view?.exception("Неизвестный символ: \"$currentSymb\" на позиции ${++position}")
        }
    }
    private fun getFalseResult(): Double {
        check('?')
        stepZero()
        check(':')
        return stepZero()
    }

    private fun getTrueResult(): Double {
        check('?')
        val x = stepOne()
        check(':')
        stepOne()
        return x
    }
}