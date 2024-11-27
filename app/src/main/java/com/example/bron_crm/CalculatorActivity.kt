package com.example.bron_crm

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Stack
import kotlin.math.pow

class CalculatorActivity : AppCompatActivity() {

    private lateinit var calculatorInput: TextView
    private var inputString = ""
    private var operator = ""
    private var firstOperand = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calculator)

        calculatorInput = findViewById(R.id.calculatorInput)

        val buttons = listOf(
            R.id.btnAC, R.id.btnPower, R.id.btnMultiply, R.id.btnDivide,
            R.id.btnPlus, R.id.btnMinus1, R.id.btnSubmit, R.id.btn1,
            R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (buttonId in buttons) {
            findViewById<Button>(buttonId).setOnClickListener(this::onButtonClick)
        }

    }

    private fun onButtonClick(view: View) { // Check the log in Logcat

        when (val button = view as Button) {
            // AC Button: Clear all
            findViewById<Button>(R.id.btnAC) -> {
                inputString = ""
                operator = ""
                firstOperand = 0.0
                updateDisplay("")
            }

            // Submit Button: Perform the calculation
            findViewById<Button>(R.id.btnSubmit) -> {
                performCalculation()
            }

            // Operator Buttons
            findViewById<Button>(R.id.btnPower),
            findViewById<Button>(R.id.btnMultiply),
            findViewById<Button>(R.id.btnDivide),
            findViewById<Button>(R.id.btnPlus),
            findViewById<Button>(R.id.btnMinus1) -> {
                handleOperator(button.text.toString())
            }

            // Number Buttons
            else -> {
                handleNumber(button.text.toString())
            }
        }
    }

    private fun handleNumber(number: String) {
        // Append number to input string
        inputString += number
        updateDisplay(inputString)
    }

    private fun handleOperator(selectedOperator: String) {
        if (inputString.isNotEmpty()) {
            operator = selectedOperator
            inputString += operator
            updateDisplay(inputString)
        }
    }



    fun evaluateExpression(input: String): Double {
        // Tokenize the input string into numbers and operators
        val tokens = tokenize(input)

        // Convert infix expression to postfix notation
        val postfix = infixToPostfix(tokens)

        // Evaluate the postfix expression
        return evaluatePostfix(postfix)
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var number = StringBuilder()

        for (char in expression) {
            if (char.isDigit() || char == '.') {
                number.append(char) // Build number
            } else {
                if (number.isNotEmpty()) {
                    tokens.add(number.toString())
                    number = StringBuilder()
                }
                tokens.add(char.toString()) // Add operator
            }
        }

        if (number.isNotEmpty()) {
            tokens.add(number.toString())
        }

        return tokens
    }

    private fun infixToPostfix(tokens: List<String>): List<String> {
        val precedence = mapOf(
            "+" to 1, "-" to 1,
            "*" to 2, "/" to 2, "%" to 2,
            "^" to 3
        )
        val output = mutableListOf<String>()
        val operators = Stack<String>()

        for (token in tokens) {
            when {
                token.isDouble() -> output.add(token) // Add number to output
                token in precedence -> {
                    while (operators.isNotEmpty() &&
                        precedence[operators.peek()] ?: 0 >= precedence[token]!!
                    ) {
                        output.add(operators.pop())
                    }
                    operators.push(token)
                }
                token == "(" -> operators.push(token)
                token == ")" -> {
                    while (operators.isNotEmpty() && operators.peek() != "(") {
                        output.add(operators.pop())
                    }
                    if (operators.isNotEmpty() && operators.peek() == "(") {
                        operators.pop()
                    }
                }
            }
        }

        while (operators.isNotEmpty()) {
            output.add(operators.pop())
        }

        return output
    }

    private fun evaluatePostfix(postfix: List<String>): Double {
        val stack = Stack<Double>()

        for (token in postfix) {
            when {
                token.isDouble() -> stack.push(token.toDouble())
                else -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(performOperation(a, b, token))
                }
            }
        }

        return stack.pop()
    }

    private fun performOperation(a: Double, b: Double, operator: String): Double {
        return when (operator) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> a / b
            "%" -> a % b
            "^" -> a.pow(b)
            else -> throw IllegalArgumentException("Unknown operator $operator")
        }
    }

    private fun String.isDouble(): Boolean {
        return this.toDoubleOrNull() != null
    }

    // Example usage in your performCalculation method:
    private fun performCalculation() {
        if (inputString.isEmpty()) return
        try {
            val result = evaluateExpression(inputString)
            updateDisplay(result.toString())
        } catch (e: Exception) {
            updateDisplay("Error")
        }
    }


    private fun updateDisplay(value: String) {
        inputString = value;
        calculatorInput.text = value
    }
}
