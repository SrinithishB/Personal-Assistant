package com.example.personalassistant;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameActivity extends AppCompatActivity {
    Button[] buttons = new Button[9];
    String currentPlayer = "X";
    boolean gameActive = true;
    TextView statusText;

    int[][] winPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
            {0, 4, 8}, {2, 4, 6}             // Diagonals
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });statusText = findViewById(R.id.statusText);

        for (int i = 0; i < 9; i++) {
            String buttonID = "button" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = findViewById(resID);

            final int index = i;
            buttons[i].setOnClickListener(v -> handleMove(index));
        }

        findViewById(R.id.resetButton).setOnClickListener(v -> resetGame());
    }
    void handleMove(int index) {
        if (!gameActive || !buttons[index].getText().toString().equals("")) return;

        buttons[index].setText(currentPlayer);

        if (checkWin()) {
            statusText.setText("Player " + currentPlayer + " wins!");
            gameActive = false;
        } else if (isDraw()) {
            statusText.setText("It's a draw!");
            gameActive = false;
        } else {
            currentPlayer = currentPlayer.equals("X") ? "O" : "X";
            statusText.setText("Player " + currentPlayer + "'s turn");
        }
    }

    boolean checkWin() {
        for (int[] pos : winPositions) {
            String a = buttons[pos[0]].getText().toString();
            String b = buttons[pos[1]].getText().toString();
            String c = buttons[pos[2]].getText().toString();
            if (!a.equals("") && a.equals(b) && a.equals(c)) {
                // Apply strikethrough animation to winning buttons
                strikeThroughWinningButtons(pos);
                return true;
            }
        }
        return false;
    }

    void strikeThroughWinningButtons(int[] pos) {
        for (int i : pos) {
            Button button = buttons[i];
            // Apply strikethrough effect by adding a strike-through text style
            button.setPaintFlags(button.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }


    boolean isDraw() {
        for (Button btn : buttons) {
            if (btn.getText().toString().equals("")) return false;
        }
        return true;
    }

    void resetGame() {
        for (Button btn : buttons) btn.setText("");
        currentPlayer = "X";
        gameActive = true;
        statusText.setText("Player X's turn");
    }
}