package ru.alexeypodusov.sample649.charactersfeature.presentation.view.list

import android.graphics.*
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

typealias OnItemDelete = (Int) -> Unit

internal class SwipeToDelete(
    private val iconDelete: Bitmap?,
    private val onItemDelete: OnItemDelete
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.ACTION_STATE_IDLE,
    ItemTouchHelper.LEFT,
) {

    private var aspectRatio: Float = 0f
    private val paint = Paint()

    init {
        iconDelete?.let {
            aspectRatio = iconDelete.width.toFloat() / iconDelete.height.toFloat()
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onItemDelete.invoke(viewHolder.absoluteAdapterPosition)
    }

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.3f

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (dX > -c.width) {
            iconDelete?.let {
                val progress = abs(dX) / (viewHolder.itemView.width)
                val maxHeightDiff = viewHolder.itemView.height / 3.toFloat() - viewHolder.itemView.height / 5.toFloat()
                val additionalHeightByProgress = maxHeightDiff * progress
                val heightIcon = viewHolder.itemView.height / 5 + additionalHeightByProgress
                val widthIcon = heightIcon * aspectRatio

                val dst = RectF(
                    viewHolder.itemView.right - viewHolder.itemView.marginEnd - widthIcon,
                    viewHolder.itemView.top + viewHolder.itemView.height / 2 - heightIcon / 2,
                    viewHolder.itemView.right.toFloat() - viewHolder.itemView.marginEnd,
                    viewHolder.itemView.top + viewHolder.itemView.height / 2 + heightIcon / 2
                )
                c.drawBitmap(iconDelete, null, dst, paint)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


}