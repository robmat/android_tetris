package com.batodev.tetris.presentation.game.grid

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.batodev.tetris.presentation.game.grid.colors.ColorCellChooser
import game.GameCell

class GameAdapter(
    var gameCells: List<GameCell>,
    private val colorChooser: ColorCellChooser,
) : BaseAdapter() {

    companion object {
        private const val GRID_COLUMNS = 10
        private const val GRID_ROWS = 20
    }

    override fun getCount(): Int = GRID_COLUMNS * GRID_ROWS

    override fun getItem(p0: Int): Any = gameCells[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?) = colorChooser.paint(gameCells[p0], p1, p2)
}
