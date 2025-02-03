package com.example.personalassistant;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Stack;

public class CalculatorActivity extends AppCompatActivity {
    Button n1, n2, n3, n4, n5, n6, n7, n8, n9, n0;
    Button btnAdd, btnSub, btnMul, btnDiv, btnDot, btnAC, btnBackspace, btnEqual;
    Button btnOpenBracket, btnCloseBracket; // Added bracket buttons
    TextView editText;
    StringBuilder currentExpression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons
        n1 = findViewById(R.id.n1);
        n2 = findViewById(R.id.n2);
        n3 = findViewById(R.id.n3);
        n4 = findViewById(R.id.n4);
        n5 = findViewById(R.id.n5);
        n6 = findViewById(R.id.n6);
        n7 = findViewById(R.id.n7);
        n8 = findViewById(R.id.n8);
        n9 = findViewById(R.id.n9);
        n0 = findViewById(R.id.n0);

        btnAdd = findViewById(R.id.btnAdd);
        btnSub = findViewById(R.id.btnSub);
        btnMul = findViewById(R.id.btnMul);
        btnDiv = findViewById(R.id.btnDiv);
        btnDot = findViewById(R.id.btnDot);
        btnAC = findViewById(R.id.btnAC);
        btnBackspace = findViewById(R.id.btnBack);
        btnEqual = findViewById(R.id.btnEqual);
        btnOpenBracket = findViewById(R.id.btnOpenBracket);
        btnCloseBracket = findViewById(R.id.btnCloseBracket);

        editText = findViewById(R.id.edit_text);

        // Number buttons
        n1.setOnClickListener(v -> appendToExpression("1"));
        n2.setOnClickListener(v -> appendToExpression("2"));
        n3.setOnClickListener(v -> appendToExpression("3"));
        n4.setOnClickListener(v -> appendToExpression("4"));
        n5.setOnClickListener(v -> appendToExpression("5"));
        n6.setOnClickListener(v -> appendToExpression("6"));
        n7.setOnClickListener(v -> appendToExpression("7"));
        n8.setOnClickListener(v -> appendToExpression("8"));
        n9.setOnClickListener(v -> appendToExpression("9"));
        n0.setOnClickListener(v -> appendToExpression("0"));

        // Operator buttons
        btnAdd.setOnClickListener(v -> appendToExpression("+"));
        btnSub.setOnClickListener(v -> appendToExpression("-"));
        btnMul.setOnClickListener(v -> appendToExpression("*"));
        btnDiv.setOnClickListener(v -> appendToExpression("/"));
        btnDot.setOnClickListener(v -> appendToExpression("."));

        // Bracket buttons
        btnOpenBracket.setOnClickListener(v -> appendToExpression("("));
        btnCloseBracket.setOnClickListener(v -> appendToExpression(")"));

        // Clear button
        btnAC.setOnClickListener(v -> {
            currentExpression.setLength(0);
            editText.setText("");
        });

        // Backspace button
        btnBackspace.setOnClickListener(v -> {
            if (currentExpression.length() > 0) {
                currentExpression.deleteCharAt(currentExpression.length() - 1);
                editText.setText(currentExpression.toString());
            }
        });

        // Equals button
        btnEqual.setOnClickListener(v -> evaluateExpression());
    }

    private void appendToExpression(String value) {
        currentExpression.append(value);
        editText.setText(currentExpression.toString());
    }

    private void evaluateExpression() {
        try {
            String expression = currentExpression.toString();
            double result = calculate(expression);
            editText.setText(String.valueOf(result));
            currentExpression.setLength(0);
            currentExpression.append(result);
        } catch (Exception e) {
            editText.setText("Error");
        }
    }

    private double calculate(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int i = 0;

        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    num.append(expression.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(num.toString()));
                continue;
            }

            if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop(); // Remove the '(' from stack
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
            i++;
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    private double applyOperator(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
        }
        return 0;
    }
}
