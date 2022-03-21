package com.sangjin.calculator_app



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import java.lang.NumberFormatException


//버튼을 클릭했을때 버튼마다 메소드를 실행해야함.
//15자를 넘기지 않게 하는 조건 : 배열(숫자 연산자 숫자)에 값이 하나라도 있어야함. / 배열의 마지막 값이 15자를 넘지 않아야함.
//0을 먼저 입력되지 않게 하는 조건 : 배열의 마지막 값에 값이 없을때 / 누른 버튼이 0일때
//연산자가 먼저 입력되지 않게 하는 조건 : 배열에 값이 없을때 / 연산자가 하나있으면 그 연산자를 지우고 새로 입력한 연산자를 추가 / 이미 텍스트뷰에 연산자를 입력한 상태일때
//연산자가 전체 텍스트중에 하나만 입력할수 있게 하는 조건 : return 을 통해서 입력을 방지하고 Toast 메세지를 띄운다.
//입력을 했을때 연산자만 색이 변하게 설정 / ssb
//결과버튼을 눌렀을때 예외 조건 : 배열이 비어있을때 / 연산자를 입력하지 않았을때 / 배열의 갯수가 3개가 아닐때 / 배열의 첫번째와 세번째가 숫자가 아닐때(isNotnumber())
//데이터베이스는 온크리에이트 메소드가 실행될 시점에 생성하여야 한다.


class MainActivity : AppCompatActivity() {

    private val ExpressionTextView by lazy {
        findViewById<TextView>(R.id.expression_tv)
    }
    private val resultTextView by lazy {
        findViewById<TextView>(R.id.result_tv)
    }
    private val historyCloseButton by lazy {
        findViewById<Button>(R.id.historyCloseButton)
    }
    private val historyClearButton by lazy {
        findViewById<Button>(R.id.historyClearButton)
    }
    private val historyLayout by lazy {
        findViewById<View>(R.id.history)
    }
    private val historyLinearLayout by lazy {
        findViewById<LinearLayout>(R.id.historyLinearLayout)
    }
    lateinit var db: AppDatabase

    private var isOperator = false
    private var hasOperator = false


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
            R.id.plusButton -> operatorButtonClicked("+")
            R.id.minusButton -> operatorButtonClicked("-")
            R.id.multiflyButton -> operatorButtonClicked("x")
            R.id.dividerButton -> operatorButtonClicked("/")
            R.id.moduloButton -> operatorButtonClicked("%")
        }
    }


    private fun numberButtonClicked(number: String) {
        if (isOperator) {
            ExpressionTextView.append(" ")
        }
        isOperator = false

        val expressionTexts = ExpressionTextView.text.split(" ")

        if (expressionTexts.isNotEmpty() && expressionTexts.last().length >= 15) {
            Toast.makeText(this, "최대 15자까지 입력할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (expressionTexts.last().isEmpty() && number == "0") {
            Toast.makeText(this, "다른 숫자를 먼저 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        ExpressionTextView.append(number)
        resultTextView.text = calculate()
    }

    private fun operatorButtonClicked(ope: String) {

        if (ExpressionTextView.text.isEmpty()) {
            return
        }

        when { //몰랐던 부분 : when 문을 사용해서 예외사항을 처리하는것.
            isOperator -> {
                val expressionText = ExpressionTextView.text.toString()
                 ExpressionTextView.text =  expressionText.dropLast(1) + ope
            }
            hasOperator -> {
                Toast.makeText(this, "연산자는 한번만 입력할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            else -> ExpressionTextView.append(" $ope")
        }

        val ssb = SpannableStringBuilder(ExpressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.greenBackground)), //몰랐던 부분 : getColor 를 사용해서 색상값을 가져오는것
            ExpressionTextView.text.length - 1, //몰랐던 부분 : 텍스트뷰의 텍스트값을 가져와서 그것의 길이를 값으로 삼는것
            ExpressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ExpressionTextView.text = ssb // 몰랐던 부분 : ssb로 변경한 내용을 텍스트뷰의 값으로 넣어준다.

        isOperator = true
        hasOperator = true
    }

    fun resultButtonClicked(view: View) {
        val expressioontexts = ExpressionTextView.text.split(" ")

        if (ExpressionTextView.text.isEmpty() || expressioontexts.size == 1) {
            return
        }
        if (expressioontexts.size != 3 && hasOperator) {
            Toast.makeText(this, "계산할 값을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (expressioontexts[0].isNotNumber() && expressioontexts[2].isNotNumber()) {
            Toast.makeText(this, "오류가 일어났습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val expressionText = ExpressionTextView.text.toString()
        val resultText = calculate()
        Thread(Runnable {
            db.historyDao().insertHistory(history(null, expressionText, resultText))
        }).start()


        resultTextView.text = ""
        ExpressionTextView.text = resultText

        isOperator = false
        hasOperator = false

    }

    private fun calculate(): String { //
        val expressiontexts = ExpressionTextView.text.split(" ")

        if (!hasOperator || expressiontexts.size != 3) { //몰랐던부분 : 연산자가 없거나 배열의 사이즈가 3이 안된때를 예외시켜야함.
            return ""
        }
        if (expressiontexts[0].isNotNumber() || expressiontexts[2].isNotNumber()) { // 몰랐던 부분 : 배열의 첫번째와 세번째가 숫자가 아닐때
            return ""
        }

        val exp1 = expressiontexts[0].toBigInteger() //몰랐던것 : String 타입을 Int 타입으로 바꾸는것
        val exp2 = expressiontexts[2].toBigInteger()
        val ope = expressiontexts[1]

        return when (ope) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "x" -> (exp1 * exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""
        }
    }

    fun clearButtonClicked(view: View) {
        ExpressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }

    fun String.isNotNumber(): Boolean {
        return try { // 몰랐던 부분 : return 을 try,catch 자체를 반환한다.
            this.toBigInteger() // 몰랐던 부분 : this 키워드를 사용한다.
            false
        } catch (e: NumberFormatException) {
            true
        }
    }

    fun historyButtonClicked(view: View) {
        historyLayout.isVisible = true

        Thread(Runnable {
            db.historyDao().getAll().forEach { //디비에서 데이터를 불러와서 뷰에 그려야함 - 모든 데이터를 리스트 형태로 가져와서
                val historyView = layoutInflater.inflate(R.layout.history_row, null, false)
                runOnUiThread { //유아이를 그려야하기 때문에 런온유아이스레드 함수로 각각의 데이터를 history_row 라는 만들어진 액티비티에 값으로 넣는다.
                    historyView.findViewById<TextView>(R.id.expression_tv).text = it.expression
                    historyView.findViewById<TextView>(R.id.result_tv).text = it.result
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
        //DB와 리니어레이아웃 모두 삭제
        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()
    }


}


