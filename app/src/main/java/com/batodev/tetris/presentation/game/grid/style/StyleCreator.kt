package com.batodev.tetris.presentation.game.grid.style

import com.batodev.tetris.presentation.game.grid.block.BlockPainter
import com.batodev.tetris.presentation.game.grid.colors.ColorCellChooser

interface StyleCreator {
    fun getBlockCreator() : BlockPainter
    fun getColorCellChooser(): ColorCellChooser
}