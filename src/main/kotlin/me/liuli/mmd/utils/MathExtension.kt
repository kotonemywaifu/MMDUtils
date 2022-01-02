package me.liuli.mmd.utils

import javax.vecmath.Vector4f
import kotlin.math.sin

/**
 * slerp in glm
 * TODO: verify is this code correct
 */
fun slerp(a: Vector4f, b: Vector4f, t: Float): Vector4f {
    val res = Vector4f()
    val omega: Float
    val sinom: Float
    val sclp: Float
    val sclq: Float
    val cosom = a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w
    if (1.0f + cosom > Math.E) {
        if (1.0f - cosom > Math.E) {
            omega = Math.acos(cosom.toDouble()).toFloat()
            sinom = Math.sin(omega.toDouble()).toFloat()
            sclp = Math.sin(((1.0f - t) * omega).toDouble()).toFloat() / sinom
            sclq = Math.sin((t * omega).toDouble()).toFloat() / sinom
        } else {
            sclp = 1.0f - t
            sclq = t
        }
        res.x = sclp * a.x + sclq * b.x
        res.y = sclp * a.y + sclq * b.y
        res.z = sclp * a.z + sclq * b.z
        res.w = sclp * a.w + sclq * b.w
    } else {
        res.w = a.z
        sclp = sin((1.0f - t) * Math.PI * 0.5f).toFloat()
        sclq = sin(t * Math.PI * 0.5f).toFloat()
        res.x = sclp * a.x + sclq * b.x
        res.y = sclp * a.y + sclq * b.y
        res.z = sclp * a.z + sclq * b.z
    }
    return res
}

fun clamp(num: Float, min: Float, max: Float): Float {
    return if (num < min) min else if (num > max) max else num
}

fun clamp(num: Int, min: Int, max: Int): Int {
    return if (num < min) min else if (num > max) max else num
}

fun degrees(radians: Float): Float {
    return radians * 180.0f / Math.PI.toFloat()
}