package me.liuli.mmd.utils

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