package com.batodev.tetris.presentation.game.grid.colors

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import board.Cell
import com.batodev.tetris.R
import game.GameCell

class VibrantColorChooser(private val context: Context) : ColorCellChooser {

    override fun paint(gameCell: GameCell, p1: View?, p2: ViewGroup?): View {
        val view = p1 ?: View.inflate(context, R.layout.grid_item, null)
        val cell: TextView = view.findViewById(R.id.grid_item)
        cell.background = AppCompatResources.getDrawable(context, this.getColorForCell(gameCell))
        cell.height = p2!!.height / 20
        cell.width = p2.width / 10
        return view
    }

    private fun getColorForCell(cell: GameCell): Int {
        if (cell.isGhostBlockCell)
            return R.color.vibrant_gray
        return when (cell.cell) {
            Cell.EMPTY -> R.color.black
            Cell.I_BLOCK -> R.color.vibrant_yellow
            Cell.J_BLOCK -> R.color.vibrant_blue
            Cell.L_BLOCK -> R.color.vibrant_cyan
            Cell.SQUARE_BLOCK -> R.color.vibrant_green
            Cell.S_BLOCK -> R.color.vibrant_red
            Cell.T_BLOCK -> R.color.vibrant_magenta
            Cell.Z_BLOCK -> R.color.vibrant_purple
        }
    }

}