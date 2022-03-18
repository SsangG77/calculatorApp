package com.sangjin.calculator_app


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import java.lang.NumberFormatException


//버튼을 클릭했을때 버튼마다 메소드를 실행해야함.
//15자를 넘기지 않게 하는 조건 : 배열(숫자 연산자 숫자)에 값이 하나라도 있어야함. / 배열의 마지막 값이 15자를 넘지 않아야함.
//0을 먼저 입력되지 않게 하는 조건 : 배열의 마지막 값에 값이 없을때 / 누른 버튼이 0일때
//연산자가 먼저 입력되지 않게 하는 조건 : 배열에 값이 없을때 / 연산자가 하나있으면 그 연산자를 지우고 새로 입력한 연산자를 추가 / 이미 텍스트뷰에 연산자를 입력한 상태일때
//연산자가 전체 텍스트중에 하나만 입력할수 있게 하는 조건 : return 을 통해서 입력을 방지하고 Toast 메세지를 띄운다.
//입력을 했을때 연산자만 색이 변하게 설정 / ssb
//결과버튼을 눌렀을때 예외 조건 : 배열이 비어있을때 / 연산자를 입력하지 않았을때 / 배열의 갯수가 3개가 아닐때 / 배열의 첫번째와 세번째가 숫자가 아닐때(isNotnumber())



class MainActivity : AppCompatActivity() {

    private val expressionTextView by lazy {
        findViewById<TextView>(R.id.expression_tv)
    }
    private val resultTextView by lazy {
        findViewById<TextView>(R.id.result_tv)
    }
    private val historyCloseButton by lazy {
        findViewById<Button>(R.id.closeButton)
    }
    private val historyClearButton by lazy {
        findViewById<Button>(R.id.historyClearButton)
    }
    private val historyLayout by lazy {
        findViewById<View>(R.id.historyLinearLayout)
    }
    private val historyLinearLayout by lazy {
        findViewById<View>(R.id.historyLayout)
    }

    private var isOperator = false
    private var hasOperator = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }


    fun buttonClicked(view: View) {
        when (view.id) {
            R.id.one -> numberClicked("1")
            R.id.two -> numberClicked("2")
            R.id.three -> numberClicked("3")
            R.id.four -> numberClicked("4")
            R.id.five -> numberClicked("5")
            R.id.six -> numberClicked("6")
            R.id.seven -> numberClicked("7")
            R.id.eight -> numberClicked("8")
            R.id.nine -> numberClicked("9")
            R.id.zero -> numberClicked("0")
            R.id.multiply -> operatorClicked("x")
            R.id.divider -> operatorClicked("/")
            R.id.modulo -> operatorClicked("%")
            R.id.plus -> operatorClicked("+")
            R.id.minus -> operatorClicked("-")
        }
    }


    private fun numberClicked(number: String) {
        if (isOperator) { //연산자가 있으면 한칸 띄우고 숫자를 입력한다.
            expressionTextView.append(" ")
            isOperator = false
        }
        val expressionArray = expressionTextView.text.split(" ")
        if (expressionArray.isNotEmpty() && expressionArray.last().length >= 15) { //텍스트뷰의 값이 있고 길이가 15자를 넘지 않아야 한다.
            Toast.makeText(this, "15자를 초과할수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionArray.last()
                .isEmpty() && number == "0"
        ) { //값이 비어있고 0을 입력했을때 알림메세지를 띄운다. (입력불가능)
            Toast.makeText(this, "0은 먼저 입력될 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        expressionTextView.append(number)
        resultTextView.text = CalculatorExpression()
    }

    private fun operatorClicked(ope: String) {
        if (expressionTextView.text.isEmpty()) { //값이 없을때는 입력 불가능
            return
        }
        when {
            isOperator -> { //이미 연산자가 하나 있을때 그 연산자를 지우고 새로 입력할 연산자를 추가한다.
                val text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(1) + ope
            }
            hasOperator -> { //이미 텍스트뷰의 값에 입력된 연산자가 있으면 더 이상 입력 불가
                Toast.makeText(this, "연산자는 한번만 입력할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                expressionTextView.append(" $ope")
            }
        }

        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.greenBackground)),
            expressionTextView.text.length - 1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        expressionTextView.text = ssb
        isOperator = true
        hasOperator = true

    }

    fun clearButtonClicked(view: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }

    fun resultButtonClicked(view: View) {
        val expressionTexts = expressionTextView.text.split(" ")
        if (expressionTexts.isEmpty() || expressionTexts.size != 3) {
            Toast.makeText(this, "계산할 숫자를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (expressionTexts[0].isNotNumber() && expressionTexts[2].isNotNumber()) {
            Toast.makeText(this, "오류가 발생되었습니다", Toast.LENGTH_SHORT).show()
            return
        }
        val expressionText = expressionTextView.text.toString()
        val resultText = CalculatorExpression()

        expressionTextView.text = CalculatorExpression()
        resultTextView.text = ""

        isOperator = false
        hasOperator = false
    }

    private fun CalculatorExpression(): String {
        val expressionText = expressionTextView.text.split(" ")

        if (!hasOperator || expressionText.size != 3) { //연산자가 없고 텍스트배열의 갯수가 3이 안될때 빈값 "" 을 리턴한다.
            return ""
        } else if (expressionText[0].isNotNumber() && expressionText[2].isNotNumber()) { //텍스트배열의 첫번째숫자값과 두번째숫자값이 숫자가 아닐때
            return ""
        }
        val exp1 = expressionText[0].toBigInteger()
        val exp2 = expressionText[2].toBigInteger()
        val ope = expressionText[1]

        return when (ope) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "x" -> (exp1 * exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""
        }
    }

    fun historyButtonClicked(view: View) {
        //TODO 데이터베이스에서 모든 기록 가져오기
        //TODO 뷰에 모든기록 할당하

        historyLayout.isVisible = true
    }

    fun closeHistoryButtonClicked(view:View) {
        historyLayout.isVisible = false
    }

    fun historyClearClicked(view:View) {
        //TODO 데이터베이스에서 모든기록 삭제
        //TODO 뷰에서 모든기록삭제
    }
}

fun String.isNotNumber(): Boolean { //숫자로 변경이 되면 true 를 반환하고 NumberFormatException 이 일어나면 false 값을 반환한다.
    return try {
        this.toBigInteger()
        false
    } catch (e: NumberFormatException) {
        true
    }
}
