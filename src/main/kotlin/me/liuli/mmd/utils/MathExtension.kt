package me.liuli.mmd.utils

import javax.vecmath.Matrix3f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f
import kotlin.math.cos
import kotlin.math.sin

/**
 * slerp in glm
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

fun mix(start: Vector3f, end: Vector3f, lerp: Float): Vector3f {
    return Vector3f(start.x + lerp * (end.x - start.x), start.y + lerp * (end.y - start.y), start.z + lerp * (end.z - start.z))
}

fun mix(start: Vector4f, end: Vector4f, lerp: Float): Vector4f {
    return Vector4f(start.x + lerp * (end.x - start.x), start.y + lerp * (end.y - start.y), start.z + lerp * (end.z - start.z), start.w + lerp * (end.w - start.w))
}

fun mix(start: Float, end: Float, lerp: Float): Float {
    return start + lerp * (end - start)
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

fun setEulerZYX(vector3f: Vector3f): Matrix3f {
    return setEulerZYX(vector3f.x, vector3f.y, vector3f.z)
}

/**
 * from bullet3 https://github.com/bulletphysics/bullet3/blob/101c98cfb8fd297ebae6007fd10619f74c4a9748/src/LinearMath/btMatrix3x3.h
 */
fun setEulerZYX(x: Float, y: Float, z: Float): Matrix3f {
    val ci = cos(x)
    val cj = cos(y)
    val ch = cos(z)
    val si = sin(x)
    val sj = sin(y)
    val sh = sin(z)
    val cc = ci * ch
    val cs = ci * sh
    val sc = si * ch
    val ss = si * sh
    return Matrix3f(cj * ch, sj * sc - cs, sj * cc + ss,
        cj * sh, sj * ss + cc, sj * cs - sc,
        -sj, cj * si, cj * ci)
}