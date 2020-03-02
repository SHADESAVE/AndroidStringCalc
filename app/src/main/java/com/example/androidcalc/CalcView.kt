package com.example.androidcalc

interface CalcView {
    fun calculate(result: Double)
    fun exception(exception: String)
}