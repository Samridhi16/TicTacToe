package com.projects.mock

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.projects.mock.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var currentTurn = Turn.CROSS
    private val ROWS = 3
    private val COLS = 3
    private var board = Array(ROWS) { arrayOfNulls<String>(COLS) }

    enum class Turn{
        NOUGHT,
        CROSS
    }
    companion object{
        const val NOUGHT = "0"
        const val CROSS = "X"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.boardContainer.children.forEachIndexed{ rowIndex,it ->
            val layout = it as ViewGroup
            layout.children.forEachIndexed {colIndex, btn ->
                val view = btn as Button
                view.setOnClickListener {
                    //update button text and disable button
                    view.text = if (currentTurn==Turn.NOUGHT) NOUGHT else CROSS
                    view.isEnabled = false
                    //update the board
                    board[rowIndex][colIndex] = view.text.toString()
                    //change turn
                    currentTurn = if (currentTurn==Turn.NOUGHT) Turn.CROSS else Turn.NOUGHT
                    // Highlight the player's turn
                    if (currentTurn == Turn.NOUGHT) {
                        binding.tvNaught.setBackgroundColor(Color.RED)
                        binding.tvCross.setBackgroundColor(Color.TRANSPARENT)
                    } else {
                        binding.tvCross.setBackgroundColor(Color.RED)
                        binding.tvNaught.setBackgroundColor(Color.TRANSPARENT)
                    }

                    // Check if there's a winner
                    if (checkWin()) {
                        val winner = if (currentTurn == Turn.CROSS) "NOUGHT" else "CROSS"
                        showWinnerDialog(winner)
                    } else if (isBoardFull()) {
                        showWinnerDialog("Draw")
                    }
                }
            }
        }
    }
    private fun isBoardFull(): Boolean {
        for (i in 0 until ROWS) {
            for (j in 0 until COLS) {
                if (board[i][j] == null) {
                    return false
                }
            }
        }
        return true
    }

    private fun checkWin():Boolean{
        // Check rows
        for (i in 0 until ROWS) {
            if (board[i][0] != null && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return true
            }
        }
        // Check columns
        for (i in 0 until COLS) {
            if (board[0][i] != null && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return true
            }
        }
        // Check diagonals
        if (board[0][0] != null && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return true
        }
        if (board[0][2] != null && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return true
        }
        return false
    }


    private fun resetBoard() {
        board = Array(ROWS) { arrayOfNulls<String>(COLS) }
        binding.boardContainer.children.forEach { layout ->
            if (layout is ViewGroup) {
                layout.children.forEach { view ->
                    if (view is Button) {
                        view.text = ""
                        view.isEnabled = true
                    }
                }
            }
        }
        currentTurn = Turn.CROSS
        binding.tvNaught.setBackgroundColor(Color.TRANSPARENT)
        binding.tvCross.setBackgroundColor(Color.RED)
    }


    private fun showWinnerDialog(winner: String){
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("$winner wins!")
            .setPositiveButton("OK"){dialog,_->
                dialog.dismiss()
                resetBoard()
            }.show()
    }
}
