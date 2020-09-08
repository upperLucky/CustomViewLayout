package com.upperlucky.customviewlayout

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.children
import kotlin.math.max

/**
 * created by yunKun.wen on 2020/9/6
 * desc:
 */
class TagLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private var childBounds = mutableListOf<Rect>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec)

//        var childWidthMeasureMode = 0
//        var childWidthMeasureSize = 0

        var widthUsed = 0 //已用宽度
        var lineWidthUsed = 0; // 当前行的已用宽度
        var heightUsed = 0 // 已用高度
        var lineMaxHeight = 0 // 当前行的最高高度

        for ((index, child) in children.withIndex()) {

            measureChildWithMargins(child,widthMeasureSpec,0,heightMeasureSpec,heightUsed)

            if (widthMeasureMode != MeasureSpec.UNSPECIFIED &&
                lineWidthUsed + child.measuredWidth > widthMeasureSize) {
                // 当前行的已用宽度加上child的宽度大于该父View的宽度，换行
                lineWidthUsed = 0
                heightUsed += lineMaxHeight
                lineMaxHeight = 0
            }

            if (index >= childBounds.size) {
                childBounds.add(Rect())
            }
            var childRect = childBounds[index]
            childRect.set(lineWidthUsed,heightUsed,lineWidthUsed + child.measuredWidth,
                heightUsed + child.measuredHeight)


            lineWidthUsed += child.measuredWidth
            widthUsed = max(widthUsed, lineWidthUsed)
            lineMaxHeight = max(lineMaxHeight, child.measuredHeight)

        }
        val selfWidth = widthUsed
        val selfHeight = heightUsed + lineMaxHeight
        setMeasuredDimension(selfWidth,selfHeight)
    }

    // 测量子view需要通过xml中对子view对宽高限制即开发者对要求，结合父View的可用空间
    /*var layoutParams = child.layoutParams
    when (layoutParams.width) {
        LayoutParams.MATCH_PARENT -> {
            when (widthMeasureMode) {
                MeasureSpec.EXACTLY -> {
                    childWidthMeasureMode = MeasureSpec.EXACTLY
                    childWidthMeasureSize = widthMeasureSize - widthUsed
                }
                MeasureSpec.AT_MOST -> {
                    childWidthMeasureMode = MeasureSpec.AT_MOST
                    childWidthMeasureSize = widthMeasureSize - widthUsed
                }
                MeasureSpec.UNSPECIFIED -> {
                    childWidthMeasureMode = MeasureSpec.UNSPECIFIED
                    childWidthMeasureSize = 0
                } 
            }
        }
        LayoutParams.WRAP_CONTENT -> {
            when (widthMeasureMode) {
                MeasureSpec.EXACTLY , MeasureSpec.AT_MOST-> {
                    childWidthMeasureMode = MeasureSpec.AT_MOST
                    childWidthMeasureSize = widthMeasureSize - widthUsed
                }
                MeasureSpec.UNSPECIFIED -> {
                    childWidthMeasureMode = MeasureSpec.UNSPECIFIED
                    childWidthMeasureSize = 0
                }
            }
        }
    }*/

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for ((index, child) in children.withIndex()) {
            var rect = childBounds[index]
            child.layout(rect.left, rect.top, rect.right, rect.bottom)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context,attrs)
    }

    override fun shouldDelayChildPressedState(): Boolean {
        // 针对不能滑动的ViewGroup 不设置是否要延迟等待子view的按下状态
        return false
    }
}
