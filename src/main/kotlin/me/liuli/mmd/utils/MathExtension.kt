package me.liuli.mmd.utils

import me.liuli.mmd.utils.vector.instance.Quat
import javax.vecmath.Matrix3f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f
import kotlin.math.*

/**
 * slerp in glm
 */
fun slerp(a: Quat, b: Quat, t: Float): Quat {
    val res = Quat()
    val omega: Float
    val sinom: Float
    val sclp: Float
    val sclq: Float
    val cosom = a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w
    if (1.0f + cosom > Math.E) {
        if (1.0f - cosom > Math.E) {
            omega = acos(cosom.toDouble()).toFloat()
            sinom = sin(omega.toDouble()).toFloat()
            sclp = sin(((1.0f - t) * omega).toDouble()).toFloat() / sinom
            sclq = sin((t * omega).toDouble()).toFloat() / sinom
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

fun clamp(vector3f: Vector3f, min: Float, max: Float): Vector3f {
    return Vector3f(clamp(vector3f.x, min, max), clamp(vector3f.y, min, max), clamp(vector3f.z, min, max))
}

fun clamp(vector3f: Vector3f, min: Vector3f, max: Vector3f): Vector3f {
    return Vector3f(clamp(vector3f.x, min.x, max.x), clamp(vector3f.y, min.y, max.y), clamp(vector3f.z, min.z, max.z))
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

fun decompose(m: Matrix3f, before: Vector3f): Vector3f {
    val r = Vector3f()
    val sy = -m.m02
    val e = 1.0e-6f
    val pi = Math.PI.toFloat()
    if((1 - abs(sy)) < e) {
        r.y = asin(sy)
        // 找到更接近180度的值
        val sx = sin(before.x)
        val sz = sin(before.z)
        if(abs(sx) < abs(sz)) {
            // X为0或180
            val cx = cos(before.x)
            if(cx > 0) {
                r.x = 0f
                r.z = asin(-m.m10)
            } else {
                r.x = pi
                r.z = asin(m.m10)
            }
        } else {
            val cz = cos(before.z)
            if (cz > 0) {
                r.x = asin(-m.m21)
                r.z = 0f
            } else {
                r.x = asin(m.m21)
                r.z = pi
            }
        }
    } else {
        r.x = atan2(m.m12, m.m22)
        r.y = asin(-m.m02)
        r.z = atan2(m.m01, m.m00)
    }

    val tests = arrayOf(Vector3f(r.x + pi, pi - r.y, r.z + pi),
        Vector3f(r.x + pi, pi - r.y, r.z - pi),
        Vector3f(r.x + pi, -pi - r.y, r.z + pi),
        Vector3f(r.x + pi, -pi - r.y, r.z - pi),
        Vector3f(r.x - pi, pi - r.y, r.z + pi),
        Vector3f(r.x - pi, pi - r.y, r.z - pi),
        Vector3f(r.x - pi, -pi - r.y, r.z + pi),
        Vector3f(r.x - pi, -pi - r.y, r.z - pi))

    val errX = abs(diffAngle(r.x, before.x))
    val errY = abs(diffAngle(r.y, r.y))
    val errZ = abs(diffAngle(r.z, r.z))
    var minErr = errX + errY + errZ
    for(test in tests) {
        val err = abs(diffAngle(test.x, before.x)) +
                abs(diffAngle(test.y, before.y)) +
                abs(diffAngle(test.z, before.z))
        if(err < minErr) {
            minErr = err
            r.set(test)
        }
    }
    return r
}

fun normalizeAngle(angle: Float): Float {
    val twoPi = Math.PI.toFloat() * 2

    return if(angle > twoPi) {
        angle % twoPi
    } else if (angle < twoPi) {
        twoPi - (angle % twoPi)
    } else {
        angle
    }
}

fun diffAngle(a: Float, b: Float): Float {
    val diff = normalizeAngle(a) - normalizeAngle(b)
    return if(diff > Math.PI.toFloat()) {
        diff - (Math.PI.toFloat() * 2)
    } else if (diff < -Math.PI.toFloat()) {
        diff + (Math.PI.toFloat() * 2)
    } else {
        diff
    }
}