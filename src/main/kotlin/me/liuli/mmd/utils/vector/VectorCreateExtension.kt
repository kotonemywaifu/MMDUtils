package me.liuli.mmd.utils.vector

import javax.vecmath.Matrix3f
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

fun mat4f(value: Float): Matrix4f {
    return Matrix4f().apply {
        m00 = value
        m11 = value
        m22 = value
        m33 = value
    }
}

fun vec4f(value: Float): Vector4f {
    return Vector4f(value, value, value, value)
}

fun vec4f(vector3f: Vector3f, w: Float): Vector4f {
    return Vector4f(vector3f.x, vector3f.y, vector3f.z, w)
}

fun vec3f(value: Float): Vector3f {
    return Vector3f(value, value, value)
}

fun vec3f(vector4f: Vector4f): Vector3f {
    return Vector3f(vector4f.x, vector4f.y, vector4f.z)
}

fun mat3f(matrix4f: Matrix4f): Matrix3f {
    return Matrix3f(matrix4f.m00, matrix4f.m01, matrix4f.m02,
        matrix4f.m10, matrix4f.m11, matrix4f.m12,
        matrix4f.m20, matrix4f.m21, matrix4f.m22)
}