package me.liuli.mmd.utils

import javax.vecmath.Matrix4f
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

fun Vector2f.multiply(x: Float, y: Float): Vector2f {
    this.x *= x
    this.y *= y
    return this
}

fun Vector3f.multiply(x: Float, y: Float, z: Float): Vector3f {
    this.x *= x
    this.y *= y
    this.z *= z
    return this
}

fun Vector4f.multiply(x: Float, y: Float, z: Float, w: Float): Vector4f {
    this.x *= x
    this.y *= y
    this.z *= z
    this.w *= w
    return this
}

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

fun mat4f(value: Float): Matrix4f {
    return Matrix4f().apply {
        m00 = value
        m11 = value
        m22 = value
        m33 = value
    }
}