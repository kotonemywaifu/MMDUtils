package me.liuli.mmd.utils.vector.operator

import javax.vecmath.Vector4f

operator fun Vector4f.plus(vector4f: Vector4f): Vector4f {
    val vec = Vector4f()
    vec.x = x + vector4f.x
    vec.y = y + vector4f.y
    vec.z = z + vector4f.z
    vec.w = w + vector4f.w
    return vec
}

operator fun Vector4f.times(vector4f: Vector4f): Vector4f {
    val vec = Vector4f()
    vec.x = x * vector4f.x
    vec.y = y * vector4f.y
    vec.z = z * vector4f.z
    vec.w = w * vector4f.w
    return vec
}

operator fun Vector4f.minus(vector4f: Vector4f): Vector4f {
    val vec = Vector4f()
    vec.x = x - vector4f.x
    vec.y = y - vector4f.y
    vec.z = z - vector4f.z
    vec.w = w - vector4f.w
    return vec
}

operator fun Vector4f.div(vector4f: Vector4f): Vector4f {
    val vec = Vector4f()
    vec.x = x / vector4f.x
    vec.y = y / vector4f.y
    vec.z = z / vector4f.z
    vec.w = w / vector4f.w
    return vec
}
operator fun Vector4f.plus(value: Float): Vector4f {
    val vec = Vector4f()
    vec.x = x + value
    vec.y = y + value
    vec.z = z + value
    vec.w = w + value
    return vec
}

operator fun Vector4f.times(value: Float): Vector4f {
    val vec = Vector4f()
    vec.x = x * value
    vec.y = y * value
    vec.z = z * value
    vec.w = w * value
    return vec
}

operator fun Vector4f.minus(value: Float): Vector4f {
    val vec = Vector4f()
    vec.x = x - value
    vec.y = y - value
    vec.z = z - value
    vec.w = w - value
    return vec
}

operator fun Vector4f.div(value: Float): Vector4f {
    val vec = Vector4f()
    vec.x = x / value
    vec.y = y / value
    vec.z = z / value
    vec.w = w / value
    return vec
}

operator fun Vector4f.get(index: Int): Float {
    return when (index) {
        0 -> x
        1 -> y
        2 -> z
        3 -> w
        else -> throw IndexOutOfBoundsException("index must be in 0..3")
    }
}

operator fun Vector4f.set(index: Int, value: Float) {
    when (index) {
        0 -> x = value
        1 -> y = value
        2 -> z = value
        3 -> w = value
        else -> throw IndexOutOfBoundsException("index must be in 0..3")
    }
}