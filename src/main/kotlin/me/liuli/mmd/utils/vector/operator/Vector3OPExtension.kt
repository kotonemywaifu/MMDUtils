package me.liuli.mmd.utils.vector.operator

import javax.vecmath.Vector3f

operator fun Vector3f.plus(vector3f: Vector3f): Vector3f {
    val vec = Vector3f()
    vec.x = x + vector3f.x
    vec.y = y + vector3f.y
    vec.z = z + vector3f.z
    return vec
}

operator fun Vector3f.times(vector3f: Vector3f): Vector3f {
    val vec = Vector3f()
    vec.x = x * vector3f.x
    vec.y = y * vector3f.y
    vec.z = z * vector3f.z
    return vec
}

operator fun Vector3f.minus(vector3f: Vector3f): Vector3f {
    val vec = Vector3f()
    vec.x = x - vector3f.x
    vec.y = y - vector3f.y
    vec.z = z - vector3f.z
    return vec
}

operator fun Vector3f.div(vector3f: Vector3f): Vector3f {
    val vec = Vector3f()
    vec.x = x / vector3f.x
    vec.y = y / vector3f.y
    vec.z = z / vector3f.z
    return vec
}

operator fun Vector3f.div(value: Float): Vector3f {
    val vec = Vector3f()
    vec.x = x / value
    vec.y = y / value
    vec.z = z / value
    return vec
}

operator fun Vector3f.plus(value: Float): Vector3f {
    val vec = Vector3f()
    vec.x = x + value
    vec.y = y + value
    vec.z = z + value
    return vec
}

operator fun Vector3f.times(value: Float): Vector3f {
    val vec = Vector3f()
    vec.x = x * value
    vec.y = y * value
    vec.z = z * value
    return vec
}

operator fun Vector3f.minus(value: Float): Vector3f {
    val vec = Vector3f()
    vec.x = x - value
    vec.y = y - value
    vec.z = z - value
    return vec
}

operator fun Vector3f.get(index: Int): Float {
    return when (index) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IllegalArgumentException("index must be 0, 1 or 2")
    }
}

operator fun Vector3f.set(index: Int, value: Float) {
    when (index) {
        0 -> x = value
        1 -> y = value
        2 -> z = value
        else -> throw IllegalArgumentException("index must be 0, 1 or 2")
    }
}