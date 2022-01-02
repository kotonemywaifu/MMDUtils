package me.liuli.mmd.utils

import javax.vecmath.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun Vector2f.mul(x: Float, y: Float): Vector2f {
    this.x *= x
    this.y *= y
    return this
}

fun Vector2f.mul(vector2f: Vector2f): Vector2f {
    this.mul(vector2f.x, vector2f.y)
    return this
}

fun Vector3f.mul(x: Float, y: Float, z: Float): Vector3f {
    this.x *= x
    this.y *= y
    this.z *= z
    return this
}

fun Vector3f.mul(vector: Vector3f): Vector3f {
    this.mul(vector.x, vector.y, vector.z)
    return this
}

fun Vector4f.mul(x: Float, y: Float, z: Float, w: Float): Vector4f {
    this.x *= x
    this.y *= y
    this.z *= z
    this.w *= w
    return this
}

fun Vector4f.mul(vector4f: Vector4f): Vector4f {
    this.mul(vector4f.x, vector4f.y, vector4f.z, vector4f.w)
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

fun Matrix4f.rotate(angle: Float, vector3f: Vector3f): Matrix4f {
    val s = sin(angle)
    val c = cos(angle)
    val C = 1 - c
    val xx = vector3f.x * vector3f.x
    val xy = vector3f.x * vector3f.y
    val xz = vector3f.x * vector3f.z
    val yy = vector3f.y * vector3f.y
    val yz = vector3f.y * vector3f.z
    val zz = vector3f.z * vector3f.z
    val rm00 = xx * C + c
    val rm01 = xy * C + vector3f.z * s
    val rm02 = xz * C - vector3f.y * s
    val rm10 = xy * C - vector3f.z * s
    val rm11 = yy * C + c
    val rm12 = yz * C + vector3f.x * s
    val rm20 = xz * C + vector3f.y * s
    val rm21 = yz * C - vector3f.x * s
    val rm22 = zz * C + c
    m00 = m00 * rm00 + m10 * rm01 + m20 * rm02
    m01 = m01 * rm00 + m11 * rm01 + m21 * rm02
    m02 = m02 * rm00 + m12 * rm01 + m22 * rm02
    m03 = m03 * rm00 + m13 * rm01 + m23 * rm02
    m10 = m00 * rm10 + m10 * rm11 + m20 * rm12
    m11 = m01 * rm10 + m11 * rm11 + m21 * rm12
    m12 = m02 * rm10 + m12 * rm11 + m22 * rm12
    m13 = m03 * rm10 + m13 * rm11 + m23 * rm12
    m20 = m00 * rm20 + m10 * rm21 + m20 * rm22
    m21 = m01 * rm20 + m11 * rm21 + m21 * rm22
    m22 = m02 * rm20 + m12 * rm21 + m22 * rm22
    m23 = m03 * rm20 + m13 * rm21 + m23 * rm22

    return this
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

fun mat4f(value: Float): Matrix4f {
    return Matrix4f().apply {
        m00 = value
        m11 = value
        m22 = value
        m33 = value
    }
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

fun Vector4f.castToMat4f(): Matrix4f {
    val res = Matrix4f()

    res.m00 = 1 - 2 * this.y * this.y - 2 * this.z * this.z
    res.m01 = 2 * this.x * this.y + 2 * this.w * this.z
    res.m02 = 2 * this.x * this.z - 2 * this.w * this.y
    res.m03 = 0.0f

    res.m10 = 2 * this.x * this.y - 2 * this.w * this.z
    res.m11 = 1 - 2 * this.x * this.x - 2 * this.z * this.z
    res.m12 = 2 * this.y * this.z + 2 * this.w * this.x
    res.m13 = 0.0f

    res.m20 = 2 * this.x * this.z + 2 * this.w * this.y
    res.m21 = 2 * this.y * this.z - 2 * this.w * this.x
    res.m22 = 1 - 2 * this.x * this.x - 2 * this.y * this.y
    res.m23 = 0.0f

    res.m30 = 0.0f
    res.m31 = 0.0f
    res.m32 = 0.0f
    res.m33 = 1.0f

    return res
}

fun Vector3f.dist(vector3f: Vector3f): Float {
    return sqrt((this.x - vector3f.x).pow(2) + (this.y - vector3f.y).pow(2) + (this.z -vector3f.z).pow(2))
}

fun Matrix4f.mul(vector4f: Vector4f): Vector4f {
    return Vector4f((m00 + m10 + m20 + m30) * vector4f.x,
        (m01 + m11 + m21 + m31) * vector4f.y,
        (m02 + m12 + m22 + m32) * vector4f.z,
        (m03 + m13 + m23 + m33) * vector4f.w)
}

fun Vector3f.dot(vec3: Vector3f): Float {
    return (x * vec3.x + y * vec3.y + z * vec3.z)
}