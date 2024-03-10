package com.batodev.tetris.presentation.game.grid.block

import block_factory.BlockType

interface BlockPainter {
    fun getImageId(block: BlockType): Int
}