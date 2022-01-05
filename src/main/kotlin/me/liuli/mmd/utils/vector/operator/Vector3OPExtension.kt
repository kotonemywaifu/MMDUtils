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