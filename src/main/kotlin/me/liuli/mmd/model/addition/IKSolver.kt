package me.liuli.mmd.model.addition

import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

class IKSolver {
    lateinit var node: Node
    lateinit var target: Node
    val chains = mutableListOf<IKChain>()
    var iterateCount = 0
    var limitAngle = 0f

    class IKChain {
        lateinit var node: Node
        var enableAxisLimit = false
        val limitMax = Vector3f()
        val limitMin = Vector3f()
        val prevAngle = Vector3f()
        val saveIKRot = Vector4f(1f, 0f, 0f, 0f)
        var planeModeAngle = 0f
    }
}