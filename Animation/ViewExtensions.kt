import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Resources
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import java.security.InvalidParameterException

object Direction {
    const val Horizontal = 0
    const val Vertical = 1
}

val Float.toDp get() = this / Resources.getSystem().displayMetrics.density
val Float.toPx get() = this * Resources.getSystem().displayMetrics.density

val Int.toDp get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.toPx get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun View.collapse(to: Int, duration: Number, direction: Int = Direction.Vertical) {
    val measuredSize = when(direction) {
        Direction.Horizontal -> this.measuredWidth
        Direction.Vertical -> this.measuredHeight
        else -> throw InvalidParameterException("Invalid direction.")
    }

    val animation: Animation = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation?
        ) {
            if (interpolatedTime == 1F) {
                if (Direction.Horizontal == direction)
                    this@collapse.layoutParams.width = to
                else if (Direction.Vertical == direction)
                    this@collapse.layoutParams.height = to
            } else {
                val size = if ((measuredSize - (measuredSize * interpolatedTime).toInt()) > to)
                    measuredSize - (measuredSize * interpolatedTime).toInt()
                else
                    to

                if (Direction.Horizontal == direction)
                    this@collapse.layoutParams.width = size
                else if (direction == Direction.Vertical)
                    this@collapse.layoutParams.height = size
            }

            this@collapse.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    animation.duration = duration.toLong()
    this.fadeOut(animation.duration)
    this.startAnimation(animation)
}

fun View.expand(to: Int, duration: Number, direction: Int = Direction.Vertical, wrapContent: Boolean = false) {
    val matchParentMeasureSpec: Int = View.MeasureSpec.makeMeasureSpec(
        when(direction){
                       Direction.Horizontal -> (this.parent as View).height
                       Direction.Vertical -> (this.parent as View).width
                       else -> throw InvalidParameterException("Invalid direction.")},
        View.MeasureSpec.EXACTLY
    )

    val wrapContentMeasureSpec: Int = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

    if(Direction.Horizontal == direction)
        this.measure(wrapContentMeasureSpec, matchParentMeasureSpec)
    else if (Direction.Vertical == direction)
        this.measure(matchParentMeasureSpec, wrapContentMeasureSpec)

    val measuredSize: Int = when {
        wrapContent.not() -> to
        Direction.Horizontal == direction && wrapContent -> this.measuredWidth
        Direction.Vertical == direction && wrapContent -> this.measuredHeight
        else -> this.measuredHeight
    }

    val from = when(direction) {
        Direction.Horizontal -> this.width
        Direction.Vertical -> this.height
        else -> throw InvalidParameterException("Invalid direction.")
    }

    if (Direction.Horizontal == direction)
        this.layoutParams.width = from
    else if (Direction.Vertical == direction)
        this.layoutParams.height = from

    this.visibility = View.VISIBLE

    val animation: Animation = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation?
        ) {
            if (interpolatedTime == 1F) {
                if (Direction.Horizontal == direction)
                    this@expand.layoutParams.width = measuredSize
                else if (Direction.Vertical == direction)
                    this@expand.layoutParams.height = measuredSize
            } else {
                val size =
                    if ((measuredSize * interpolatedTime).toInt() < from)
                        from
                    else
                        (measuredSize * interpolatedTime).toInt()

                if (Direction.Horizontal == direction)
                    this@expand.layoutParams.width = size
                else
                    this@expand.layoutParams.height = size
            }
            this@expand.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    animation.duration = duration.toLong()
    this.fadeIn(animation.duration)
    this.startAnimation(animation)
}

fun View.fadeIn(duration: Number) {
    this.apply {
        alpha = 0F
        visibility = View.VISIBLE

        animate()
            .alpha(1F)
            .setDuration(duration.toLong())
            .setListener(null)
    }
}

fun View.fadeOut(duration: Number, onAnimationEnd: (() -> Unit)? = null) {
    this.apply {
        alpha = 1F
        visibility = View.VISIBLE

        animate()
            .alpha(0F)
            .setDuration(duration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    this@fadeOut.visibility = View.GONE
                    onAnimationEnd?.invoke()
                    super.onAnimationEnd(animation)
                }
            })
    }
}
