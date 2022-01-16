package me.liuli.mmd.utils.vector.instance

import me.liuli.mmd.utils.vector.mat4f
import me.liuli.mmd.utils.vector.operator.plus
import me.liuli.mmd.utils.vector.operator.times
import javax.vecmath.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class Quat : Tuple4f {

    constructor() : super()

    constructor(w: Float, x: Float, y: Float, z: Float) : super(x, y, z, w)

    constructor(q: Quat) : super(q.x, q.y, q.z, q.w)

    constructor(v3: Vector3f, w: Float) : super(v3.x, v3.y, v3.z, w)

    constructor(q: Tuple4f) : super(q.x, q.y, q.z, q.w)

    fun vec3(): Vector3f {
        return Vector3f(x, y, z)
    }

    fun vec4(): Vector4f {
        return Vector4f(x, y, z, w)
    }

    fun castToMat3f(): Matrix3f {
        val res = Matrix3f()

        res.m00 = 1f - 2f * ((this.y * this.y) +  (this.z * this.z))
        res.m01 = 2f * ((this.x * this.y) + (this.w * this.z))
        res.m02 = 2f * ((this.x * this.z) - (this.w * this.y))

        res.m10 = 2f * ((this.x * this.y) - (this.w * this.z))
        res.m11 = 1f - 2f * ((this.x * this.x) +  (this.z * this.z))
        res.m12 = 2f * ((this.y * this.z) + (this.w * this.x))

        res.m20 = 2f * ((this.x * this.z) + (this.w * this.y))
        res.m21 = 2f * ((this.y * this.z) - (this.w * this.x))
        res.m22 = 1f - 2f * ((this.x * this.x) +  (this.y * this.y))

        return res
    }
    
    fun castToMat4f(): Matrix4f {
        val res = mat4f(1f)
        val m3 = castToMat3f()

        res.m00 = m3.m00
        res.m01 = m3.m01
        res.m02 = m3.m02
        res.m10 = m3.m10
        res.m11 = m3.m11
        res.m12 = m3.m12
        res.m20 = m3.m20
        res.m21 = m3.m21
        res.m22 = m3.m22

        return res
    }

    fun rotate(angle: Float, rot: Vector3f): Quat {
        val tmp = Vector3f(rot)
        val len = rot.length()
        if(abs(len - 1) > 0.001) {
            val oneOverLen = 1 / len
            tmp.x *= oneOverLen
            tmp.y *= oneOverLen
            tmp.z *= oneOverLen
        }
        val sim = sin(angle * 0.5f)

        return (this * Quat(cos(angle * 0.5f), tmp.x * sim, tmp.y * sim, tmp.z * sim))
    }

    fun conjugate(): Quat {
        return Quat(w, -x, -y, -z)
    }

    fun inverse(): Quat {
        return conjugate() / this.vec4().let { it.dot(it) }
    }

    operator fun times(q: Quat): Quat {
        val result = Quat()

        result.x = w * q.x + x * q.w + y * q.z - z * q.y
        result.y = w * q.y + y * q.w + z * q.x - x * q.z
        result.z = w * q.z + z * q.w + x * q.y - y * q.x
        result.w = w * q.w - (x * q.x + y * q.y + z * q.z)

        return result
    }

    operator fun times(float: Float): Quat {
        val result = Quat(this)

        result.x *= float
        result.y *= float
        result.z *= float

        return result
    }

    operator fun times(v3: Vector3f): Vector3f {
        val quatVector = Vector3f(x, y, z)
        var uv = crossProduct(quatVector, v3)
        var uuv = crossProduct(quatVector, uv)
        uv *= (2.0f * w)
        uuv *= 2f
        return v3 + uv + uuv
    }

    operator fun div(float: Float): Quat {
        val result = Quat(this)

        result.x /= float
        result.y /= float
        result.z /= float

        return result
    }

    private fun crossProduct(a: Vector3f, b: Vector3f): Vector3f {
        return Vector3f(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x)
    }

//    operator fun plus(q: Quat): Quat {
//        return Quat(this.x + q.x, this.y + q.y, this.z + q.z, this.w + q.w)
//    }
}