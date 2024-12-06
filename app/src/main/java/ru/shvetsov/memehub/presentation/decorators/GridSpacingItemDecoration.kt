package ru.shvetsov.memehub.presentation.decorators

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val lineColor: Int
) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        color = lineColor
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(0, 0, 0, 0)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            // Координаты границ элементов
            val left = child.left.toFloat()
            val right = child.right.toFloat()
            val top = child.top.toFloat()
            val bottom = child.bottom.toFloat()

            // Рисуем линии:
            // Верхняя линия
            c.drawLine(left, top, right, top, paint)
            // Левая линия
            c.drawLine(left, top, left, bottom, paint)
            // Правая линия
            c.drawLine(right, top, right, bottom, paint)
            // Нижняя линия
            c.drawLine(left, bottom, right, bottom, paint)
        }
    }
}
