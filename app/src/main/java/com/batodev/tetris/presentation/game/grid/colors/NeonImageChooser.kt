package com.batodev.tetris.presentation.game.grid.colors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import board.Cell
import com.batodev.tetris.R
import game.GameCell

class NeonImageChooser(
    private val context: Context,
) : ColorCellChooser {
    companion object {
        private const val GRID_COLUMNS = 10
        private const val GRID_ROWS = 20
    }

    override fun paint(
        gameCell: GameCell,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        var convertView = convertView
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item_image, null)
        }
        val imageView: ImageView = convertView!!.findViewById(R.id.grid_item_image)
        imageView.setImageResource(getResourceForCell(gameCell))
        val params = imageView.layoutParams
        params.width = parent!!.width / GRID_COLUMNS
        params.height = parent.height / GRID_ROWS
        return convertView
    }

    private fun getResourceForCell(cell: GameCell): Int {
        if (cell.isGhostBlockCell) {
            return R.drawable.ghostcellneon
        }
        return when (cell.cell) {
            Cell.EMPTY -> R.drawable.blackcellneon
            Cell.I_BLOCK -> R.drawable.cyancellneon
            Cell.J_BLOCK -> R.drawable.greencellneon
            Cell.L_BLOCK -> R.drawable.orangecellneon
            Cell.SQUARE_BLOCK -> R.drawable.bluecellneon
            Cell.S_BLOCK -> R.drawable.purplecellneon
            Cell.T_BLOCK -> R.drawable.redcellneon
            Cell.Z_BLOCK -> R.drawable.yellowcellneon
        }
    }
}
