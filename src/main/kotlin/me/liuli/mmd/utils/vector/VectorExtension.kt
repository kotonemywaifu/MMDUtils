package me.liuli.mmd.utils.vector

import me.liuli.mmd.utils.vector.operator.times
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f
import kotlin.math.cos
import kotlin.math.sin

fun Matrix4f.translate(vector3f: Vector3f): Matrix4f {
    return translate(vector3f.x, vector3f.y, vector3f.z)
}

fun Matrix4f.translate(x: Float, y: Float, z: Float): Matrix4f {
    m30 += m00 * x + m10 * y + m20 * z
    m31 += m01 * x + m11 * y + m21 * z
    m32 += m02 * x + m12 * y + m22 * z
    m33 += m03 * x + m13 * y + m23 * z
    return this
}

fun Matrix4f.inverse(): Matrix4f {
    val a = m00 * m11 - m01 * m10
    val b = m00 * m12 - m02 * m10
    val c = m00 * m13 - m03 * m10
    val d = m01 * m12 - m02 * m11
    val e = m01 * m13 - m03 * m11
    val f = m02 * m13 - m03 * m12
    val g = m20 * m31 - m21 * m30
    val h = m20 * m32 - m22 * m30
    val i = m20 * m33 - m23 * m30
    val j = m21 * m32 - m22 * m31
    val k = m21 * m33 - m23 * m31
    val l = m22 * m33 - m23 * m32
    val det = 1f / (a * l - b * k + c * j + d * i - e * h + f * g)
    m00 = (m11 * l - m12 * k + m13 * j) * det
    m01 = (-m01 * l + m02 * k - m03 * j) * det
    m02 = (+m31 * f - m32 * e + m33 * d) * det
    m03 = (-m21 * f + m22 * e - m23 * d) * det
    m10 = (-m10 * l + m12 * i - m13 * h) * det
    m11 = (+m00 * l - m02 * i + m03 * h) * det
    m12 = (-m30 * f + m32 * c - m33 * b) * det
    m13 = (+m20 * f - m22 * c + m23 * b) * det
    m20 = (+m10 * k - m11 * i + m13 * g) * det
    m21 = (-m00 * k + m01 * i - m03 * g) * det
    m22 = (+m30 * e - m31 * c + m33 * a) * det
    m23 = (-m20 * e + m21 * c - m23 * a) * det
    m30 = (-m10 * j + m11 * h - m12 * g) * det
    m31 = (+m00 * j - m01 * h + m02 * g) * det
    m32 = (-m30 * d + m31 * b - m32 * a) * det
    m33 = (+m20 * d - m21 * b + m22 * a) * det
    return this
}

fun Matrix4f.rotate(angle: Float, vector3f: Vector3f): Matrix4f {
    val s = sin(angle)
    val c = cos(angle)
    val axis = Vector3f(vector3f).apply { normalize() }
    val temp = axis * Vector3f(1 - c, 1 - c, 1 - c)

    val r00 = c + temp.x * axis.x
    val r01 = temp.x * axis.y + s * axis.z
    val r02 = temp.x * axis.z - s * axis.y
    val r10 = temp.y * axis.x - s * axis.z
    val r11 = c + temp.y * axis.y
    val r12 = temp.y * axis.z + s * axis.x
    val r20 = temp.z * axis.x + s * axis.y
    val r21 = temp.z * axis.y - s * axis.x
    val r22 = c + temp.z * axis.z

    val res = mat4f(1f)

    res.m00 = m00 * r00 + m10 * r01 + m20 * r02
    res.m01 = m01 * r00 + m11 * r01 + m21 * r02
    res.m02 = m02 * r00 + m12 * r01 + m22 * r02
    res.m03 = m03 * r00 + m13 * r01 + m23 * r02
    res.m10 = m00 * r10 + m10 * r11 + m20 * r12
    res.m11 = m01 * r10 + m11 * r11 + m21 * r12
    res.m12 = m02 * r10 + m12 * r11 + m22 * r12
    res.m13 = m03 * r10 + m13 * r11 + m23 * r12
    res.m20 = m00 * r20 + m10 * r21 + m20 * r22
    res.m21 = m01 * r20 + m11 * r21 + m21 * r22
    res.m22 = m02 * r20 + m12 * r21 + m22 * r22
    res.m23 = m03 * r20 + m13 * r21 + m23 * r22

    return this.apply { set(res) }
}

fun Matrix4f.scale(factor: Float): Matrix4f {
    return scale(factor, factor, factor)
}

fun Matrix4f.scale(vector3f: Vector3f): Matrix4f {
    return scale(vector3f.x, vector3f.y, vector3f.z)
}

fun Matrix4f.scale(x: Float, y: Float, z: Float): Matrix4f {
    m00 *= x
    m01 *= x
    m02 *= x
    m03 *= x
    m10 *= y
    m11 *= y
    m12 *= y
    m13 *= y
    m20 *= z
    m21 *= z
    m22 *= z
    m23 *= z
    return this
}

fun Matrix4f.invZ(): Matrix4f {
    m02 *= -1
    m12 *= -1
    m20 *= -1
    m21 *= -1
    m23 *= -1
    m32 *= -1
    return this
}