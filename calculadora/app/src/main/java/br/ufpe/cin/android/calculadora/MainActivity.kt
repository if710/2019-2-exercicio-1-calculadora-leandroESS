package br.ufpe.cin.android.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var equation: String  = ""
    private var result: String = ""

   // aqui boti o marcador override para salvar o estado para quando ele destruir a activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
     // parte onde eu seto os listeners de cada botão , de acordo com item 2
        btn_0.setOnClickListener { addToExpression(btn_0.text.toString()) }
        btn_1.setOnClickListener { addToExpression(btn_1.text.toString()) }
        btn_2.setOnClickListener { addToExpression(btn_2.text.toString()) }
        btn_3.setOnClickListener { addToExpression(btn_3.text.toString()) }
        btn_4.setOnClickListener { addToExpression(btn_4.text.toString()) }
        btn_5.setOnClickListener { addToExpression(btn_5.text.toString()) }
        btn_6.setOnClickListener { addToExpression(btn_6.text.toString()) }
        btn_7.setOnClickListener { addToExpression(btn_7.text.toString()) }
        btn_8.setOnClickListener { addToExpression(btn_8.text.toString()) }
        btn_9.setOnClickListener { addToExpression(btn_9.text.toString()) }
        btn_Add.setOnClickListener { addToExpression(btn_Add.text.toString()) }
        btn_Subtract.setOnClickListener { addToExpression(btn_Subtract.text.toString()) }
        btn_Multiply.setOnClickListener { addToExpression(btn_Multiply.text.toString()) }
        btn_Divide.setOnClickListener { addToExpression(btn_Divide.text.toString()) }
        btn_Power.setOnClickListener { addToExpression(btn_Power.text.toString()) }
        btn_Dot.setOnClickListener { addToExpression(btn_Dot.text.toString()) }
        btn_LParen.setOnClickListener { addToExpression(btn_LParen.text.toString()) }
        btn_RParen.setOnClickListener { addToExpression(btn_RParen.text.toString()) }
        btn_Clear.setOnClickListener {
            cleanExpression()
            cleanInfo()
        }
        btn_Equal.setOnClickListener {// aqui eu decidi botar o tratamento de exceção detalalhada no item 4 , onde eu coloco a msg de erro da expressão
            try {
                text_calc.setText(eval(text_info.text.toString()).toString())
            }catch (er: Exception) {
                Toast.makeText(this, "Expressão invalida, tente novamente", Toast.LENGTH_LONG).show()
                cleanExpression()
            }
        }
    }
    // aqui eu vou recuperar o estado para pegar as informações antes da mudança de configuração
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("equation", text_info.text.toString())
        outState.putString("result", text_calc.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        text_info.text = savedInstanceState.getString("equation")
        text_calc.setText(savedInstanceState.getString("result"))
    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    private fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }

    private fun addToExpression(str: String) {
        text_info.text = text_info.text.toString() + str
    }

    private fun cleanExpression() {
        text_info.text = ""
    }

    private fun cleanInfo() {
        text_calc.setText("")
    }
}