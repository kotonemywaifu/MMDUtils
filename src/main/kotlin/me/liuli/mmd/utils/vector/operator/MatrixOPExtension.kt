package me.liuli.mmd.utils.vector.operator

import javax.vecmath.Matrix3f
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

operator fun Matrix4f.times(matrix4f: Matrix4f): Matrix4f {
    return Matrix4f(this).apply { mul(matrix4f) }
}

operator fun Matrix4f.timesAssign(matrix4f: Matrix4f) {
    mul(matrix4f)
}

operator fun Matrix4f.times(value: Float): Matrix4f {
    return Matrix4f(this).apply { mul(value) }
}

operator fun Matrix4f.timesAssign(value: Float) {
    mul(value)
}

operator fun Matrix4f.plusAssign(matrix4f: Matrix4f) {
    m00 += matrix4f.m00
    m01 += matrix4f.m01
    m02 += matrix4f.m02
    m03 += matrix4f.m03
    m10 += matrix4f.m10
    m11 += matrix4f.m11
    m12 += matrix4f.m12
    m13 += matrix4f.m13
    m20 += matrix4f.m20
    m21 += matrix4f.m21
    m22 += matrix4f.m22
    m23 += matrix4f.m23
    m30 += matrix4f.m30
    m31 += matrix4f.m31
    m32 += matrix4f.m32
    m33 += matrix4f.m33
}

operator fun Matrix4f.plus(matrix4f: Matrix4f): Matrix4f {
    return Matrix4f(this).apply { plusAssign(matrix4f) }
}

operator fun Matrix4f.minusAssign(matrix4f: Matrix4f) {
    m00 -= matrix4f.m00
    m01 -= matrix4f.m01
    m02 -= matrix4f.m02
    m03 -= matrix4f.m03
    m10 -= matrix4f.m10
    m11 -= matrix4f.m11
    m12 -= matrix4f.m12
    m13 -= matrix4f.m13
    m20 -= matrix4f.m20
    m21 -= matrix4f.m21
    m22 -= matrix4f.m22
    m23 -= matrix4f.m23
    m30 -= matrix4f.m30
    m31 -= matrix4f.m31
    m32 -= matrix4f.m32
    m33 -= matrix4f.m33
}

operator fun Matrix4f.minus(matrix4f: Matrix4f): Matrix4f {
    return Matrix4f(this).apply { plusAssign(matrix4f) }
}

operator fun Matrix4f.times(vector4f: Vector4f): Vector4f {
    return Vector4f((m00 + m10 + m20 + m30) * vector4f.x,
        (m01 + m11 + m21 + m31) * vector4f.y,
        (m02 + m12 + m22 + m32) * vector4f.z,
        (m03 + m13 + m23 + m33) * vector4f.w)
}

operator fun Matrix3f.times(vector3f: Vector3f): Vector3f {
    return Vector3f((m00 + m10 + m20) * vector3f.x,
        (m01 + m11 + m21) * vector3f.y,
        (m02 + m12 + m22) * vector3f.z)
}