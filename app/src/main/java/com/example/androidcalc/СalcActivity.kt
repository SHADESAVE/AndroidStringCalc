package com.example.androidcalc

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class СalcActivity : AppCompatActivity(), CalcView {

    private val presenter = CalcPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.bindView(this)

        button_calculate.setOnClickListener {
            textView_error.text = ""
            val expression = editText_expression.text.replace("\\s+".toRegex(), "")
            if (expression.isNotEmpty()) {
                textView_expression.text = expression+"="
                presenter.onButtonClick(expression)
            }
            else {
                this.exception("Введите выражение\n")
            }
        }


    }

    override fun calculate(result: Double) {
        if (textView_error.text.isEmpty()) {
            editText_expression.setText(result.toString())
            editText_expression.setSelection(editText_expression.getText().length)
        }
        else {
            editText_expression.setText("")
        }
    }

    override fun exception(exception: String) {
        textView_error.text = exception
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }
}
