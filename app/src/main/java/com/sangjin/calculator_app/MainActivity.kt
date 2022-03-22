package com.sangjin.calculator_app


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import java.lang.NumberFormatException


class MainActivity : AppCompatActivity() {
    val ExpressionTextView by lazy {
        findViewById<TextView>(R.id.expression_tv)
    }
    val ResultTextView by lazy {
        findViewById<TextView>(R.id.result_tv)
    }
    val historyLayout by lazy {
        findViewById<View>(R.id.history)
    }
    val historyLinearLayout by lazy {
        findViewById<LinearLayout>(R.id.historyLinearLayout)
    }

    lateinit var db: AppDatabase

    var isOperator = false
    var hasOperator = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "HistoryDatabase")
            .build()
    }


    fun buttonClicked(view: View) {
        when (view.id) {
            R.id.one -> numberButtonClicked("1")
            R.id.two -> numberButtonClicked("2")
            R.id.three -> numberButtonClicked("3")
            R.id.four -> numberButtonClicked("4")
            R.id.five -> numberButtonClicked("5")
            R.id.six -> numberButtonClicked("6")
            R.id.seven -> numberButtonClicked("7")
            R.id.eight -> numberButtonClicked("8")
            R.id.nine -> numberButtonClicked("9")
            R.id.zero -> numberButtonClicked("0")
            R.id.Modulo -> operatorButtonClicked("%")
            R.id.plus -> operatorButtonClicked("+")
            R.id.minus -> operatorButtonClicked("-")
            R.id.Divider -> operatorButtonClicked("/")
            R.id.Multiply -> operatorButtonClicked("x")
        }

    }

    fun numberButtonClicked(number: String) {

        if (isOperator) {
            ExpressionTextView.append(" ")
        }
        isOperator = false

        val expressionTexts = ExpressionTextView.text.split(" ")

        if (expressionTexts.isNotEmpty() && ExpressionTextView.text.length >= 15) {
            Toast.makeText(this, "15자 이상으로 입력할수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (expressionTexts.last().isEmpty() && number == "0") {
            Toast.makeText(this, "다른 숫자를 먼저 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        ExpressionTextView.append(number)
        ResultTextView.text = calculator()
    }

    fun operatorButtonClicked(ope: String) {
        val expressionTexts = ExpressionTextView.text.split(" ")
        if (expressionTexts.isEmpty()) {
            return
        }
        when {
            isOperator -> {
                ExpressionTextView.text = ExpressionTextView.text.toString().dropLast(1) + ope
            }
            hasOperator -> {
                Toast.makeText(this, "연산자는 한번만 입력할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            else -> ExpressionTextView.append(" $ope")
        }

        val ssb = SpannableStringBuilder(ExpressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.greenBackground)),
            ExpressionTextView.text.length - 1,
            ExpressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ExpressionTextView.text = ssb

        isOperator = true
        hasOperator = true

    }

    fun resultButtonClicked(view: View) {
        val expressionTexts = ExpressionTextView.text.split(" ")

        if(ExpressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }
        if(expressionTexts.size != 3 && hasOperator) {
            Toast.makeText(this, "계산할 값을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if(expressionTexts[0].isNotNumber() && expressionTexts[2].isNotNumber()) {
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val expressionText = ExpressionTextView.text.toString()
        val resultText = calculator()

        Thread(Runnable {
            db.historyDao().insertHistory(history(null, expressionText, resultText))
        }).start()

        ExpressionTextView.text = resultText
        ResultTextView.text = ""

        isOperator = false
        hasOperator = false

    }

    fun calculator(): String {
        val expressionTexts = ExpressionTextView.text.split(" ")

        if (!hasOperator || expressionTexts.size != 3) {
            return ""
        }
        if (expressionTexts[0].isNotNumber() && expressionTexts[2].isNotNumber()) {
            return ""
        }


        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()

        return when (expressionTexts[1]) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "%" -> (exp1 % exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "x" -> (exp1 * exp2).toString()
            else -> ""
        }
    }

    fun String.isNotNumber(): Boolean {
        return try {
            this.toBigInteger()
            false
        } catch (e: NumberFormatException) {
            true
        }
    }

    fun clearButtonClicked(view: View) {
        ExpressionTextView.text = ""
        ResultTextView.text = ""
    }

    fun historyButtonClicked(view: View) {
        historyLayout.isVisible = true

        Thread(Runnable {
            db.historyDao().getAll().forEach {
                val historyView = layoutInflater.inflate(R.layout.history_item, null, false)
                runOnUiThread {
                    historyView.findViewById<TextView>(R.id.historyExpressionTextview).text = it.expressionText
                    historyView.findViewById<TextView>(R.id.historyResultTextview).text = it.resultText
                    historyLinearLayout.addView(historyView)
                    }

                }
        }).start()
    }

    fun historyCloseButtonClicked(view: View) {
        historyLayout.isVisible = false
    }
    fun historyClearButtonClicked(view: View) {
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()

    }
}




