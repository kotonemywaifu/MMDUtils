package me.liuli.mmd.model.addition

import me.liuli.mmd.utils.clamp
import me.liuli.mmd.utils.decompose
import me.liuli.mmd.utils.degrees
import me.liuli.mmd.utils.vector.instance.Quat
import me.liuli.mmd.utils.vector.inverse
import me.liuli.mmd.utils.vector.operator.get
import me.liuli.mmd.utils.vector.operator.minus
import me.liuli.mmd.utils.vector.operator.times
import me.liuli.mmd.utils.vector.vec3f
import me.liuli.mmd.utils.vector.vec4f
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f
import kotlin.math.abs
import kotlin.math.acos

class IKSolver {
    lateinit var node: Node
    lateinit var target: Node
    val chains = mutableListOf<IKChain>()
    var iterateCount = 0
    var limitAngle = 0f
    var isEnable = false
    var baseAnimEnable = false

    fun clearBaseAnimation() {
        baseAnimEnable = false
    }

    fun solve() {
        if(!isEnable) return

        for(chain in chains) {
            chain.prevAngle.set(0f, 0f, 0f)
            chain.node.ikRotate.set(1f, 0f, 0f, 0f)
            chain.planeModeAngle = 0f
            chain.node.updateLocalTransform()
            chain.node.updateGlobalTransform()
        }

        var maxDist = Float.MAX_VALUE
        for (i in 0 until iterateCount) {
            solveCore(i)

            val targetPos = Vector3f(target.global.m30, target.global.m31, target.global.m32)
            val ikPos = Vector3f(node.global.m30, node.global.m31, node.global.m32)
            val dist = (ikPos - targetPos).length()
            if (dist < maxDist) {
                maxDist = dist
                for(chain in chains) {
                    chain.saveIKRot.set(chain.node.ikRotate)
                }
            } else {
                for(chain in chains) {
                    chain.node.ikRotate.set(chain.saveIKRot)
                    chain.node.updateLocalTransform()
                    chain.node.updateGlobalTransform()
                }
            }
        }
    }

    fun solveCore(iterate: Int) {
        val ikPos = Vector3f(node.global.m30, node.global.m31, node.global.m32)
        for(chain in chains) {
            val index = chains.indexOf(chain)
            val chainNode = chain.node
            if(chainNode == target) continue
            if(chain.enableAxisLimit) {
                if ((chain.limitMin.x != 0f || chain.limitMax.x != 0f) &&
                    (chain.limitMin.y == 0f || chain.limitMax.y == 0f) &&
                    (chain.limitMin.z == 0f || chain.limitMax.z == 0f)) {
                    solvePlane(iterate, index, SolveAxis.X)
                    continue
                } else if ((chain.limitMin.y != 0f || chain.limitMax.y != 0f) &&
                    (chain.limitMin.x == 0f || chain.limitMax.x == 0f) &&
                    (chain.limitMin.z == 0f || chain.limitMax.z == 0f)) {
                    solvePlane(iterate, index, SolveAxis.Y)
                    continue
                }
                else if ((chain.limitMin.z != 0f || chain.limitMax.z != 0f) &&
                    (chain.limitMin.x == 0f || chain.limitMax.x == 0f) &&
                    (chain.limitMin.y == 0f || chain.limitMax.y == 0f)) {
                    solvePlane(iterate, index, SolveAxis.Z)
                    continue
                }
            }

            val targetPos = Vector3f(target.global.m30, target.global.m31, target.global.m32)
            val invChain = Matrix4f(chain.node.global).inverse()
            val chainIkPos = vec3f(invChain * vec4f(ikPos, 1f))
            val chainTargetPos = vec3f(invChain * vec4f(targetPos, 1f))
            val chainIkVec = Vector3f(chainIkPos).apply { normalize() }
            val chainTargetVec = Vector3f(chainTargetPos).apply { normalize() }
            val dot = clamp(chainTargetVec.dot(chainIkVec), -1f, 1f)
            var angle = acos(dot)
            val angleDeg = degrees(angle)
            if (angleDeg < 1.0e-3f) {
                continue
            }
            angle = clamp(angle, -limitAngle, limitAngle)
            val cross = Vector3f().apply { cross(chainIkVec, chainTargetVec) }.apply { normalize() }
            val rot = Quat(1f, 0f, 0f, 0f).rotate(angle, cross)

            val chainRot = chainNode.ikRotate * chainNode.animRotate * rot
            if(chain.enableAxisLimit) {
                val chainRotM = chainRot.castToMat3f()
                val rotXYZ = decompose(chainRotM, chain.prevAngle)
                val clampXYZ = clamp(clamp(rotXYZ, chain.limitMin, chain.limitMax) - chain.prevAngle, -limitAngle, limitAngle)
                val r = Quat(1f, 0f, 0f, 0f).rotate(clampXYZ.x, Vector3f(1f, 0f, 0f))
                    .rotate(clampXYZ.y, Vector3f(0f, 1f, 0f))
                    .rotate(clampXYZ.z, Vector3f(0f, 0f, 1f))
                chain.prevAngle.set(clampXYZ)
                chainRot.set(r)
            }

            val ikRot = chainRot * chainNode.animRotate.inverse()
            chainNode.ikRotate.set(ikRot)

            chainNode.updateLocalTransform()
            chainNode.updateGlobalTransform()
        }
    }

    fun solvePlane(iterate: Int, index: Int, axis: SolveAxis) {
        val rotateAxisIndex: Int
        val rotateAxis = Vector3f()
        val plane = Vector3f()
        when(axis) {
            SolveAxis.X -> {
                rotateAxisIndex = 0
                rotateAxis.set(1f, 0f, 0f)
                plane.set(0f, 1f, 1f)
            }
            SolveAxis.Y -> {
                rotateAxisIndex = 1
                rotateAxis.set(0f, 1f, 0f)
                plane.set(1f, 0f, 1f)
            }
            SolveAxis.Z -> {
                rotateAxisIndex = 2
                rotateAxis.set(0f, 0f, 1f)
                plane.set(1f, 1f, 0f)
            }
        }

        val chain = chains[index]
        val ikPos = Vector3f(node.global.m30, node.global.m31, node.global.m32)
        val targetPos = Vector3f(target.global.m30, target.global.m31, target.global.m32)
        val invChain = Matrix4f(chain.node.global).inverse()
        val chainIkPos = vec3f(invChain * vec4f(ikPos, 1f))
        val chainTargetPos = vec3f(invChain * vec4f(targetPos, 1f))
        val chainIkVec = Vector3f(chainIkPos).apply { normalize() }
        val chainTargetVec = Vector3f(chainTargetPos).apply { normalize() }
        val dot = clamp(chainTargetVec.dot(chainIkVec), -1f, 1f)
        var angle = acos(dot)
//        val angleDeg = degrees(angle)
        angle = clamp(angle, -limitAngle, limitAngle)

        val rot1 = Quat(1f, 0f, 0f, 0f).rotate(angle, rotateAxis)
        val targetVec1 = rot1 * chainTargetVec
        val dot1 = targetVec1.dot(chainIkVec)

        val rot2 = Quat(1f, 0f, 0f, 0f).rotate(-angle, rotateAxis)
        val targetVec2 = rot2 * chainTargetVec
        val dot2 = targetVec2.dot(chainIkVec)

        var newAngle = chain.planeModeAngle
        if(dot1 > dot2) {
            newAngle += angle
        } else {
            newAngle -= angle
        }
        if (iterate == 0) {
            if(newAngle < chain.limitMin[rotateAxisIndex] || newAngle > chain.limitMax[rotateAxisIndex]) {
                if (-newAngle > chain.limitMin[rotateAxisIndex] && -newAngle < chain.limitMax[rotateAxisIndex]) {
                    newAngle *= -1
                } else {
                    val halfRad = (chain.limitMin[rotateAxisIndex] + chain.limitMax[rotateAxisIndex]) * 0.5f
                    if (abs(halfRad - newAngle) > abs(halfRad + newAngle)) {
                        newAngle *= -1
                    }
                }
            }
        }

        newAngle = clamp(newAngle, chain.limitMin[rotateAxisIndex], chain.limitMax[rotateAxisIndex])
        chain.planeModeAngle = newAngle

        chain.node.ikRotate.set(Quat(1f, 0f, 0f, 0f).rotate(newAngle, rotateAxis) * chain.node.animRotate.inverse())

        chain.node.updateLocalTransform()
        chain.node.updateGlobalTransform()
    }

    enum class SolveAxis {
        X, Y, Z
    }

    fun loadBaseAnimation() {
        isEnable = baseAnimEnable
    }

    fun saveBaseAnimation() {
        baseAnimEnable = isEnable
    }

    class IKChain {
        lateinit var node: Node
        var enableAxisLimit = false
        val limitMax = Vector3f()
        val limitMin = Vector3f()
        val prevAngle = Vector3f()
        val saveIKRot = Quat(1f, 0f, 0f, 0f)
        var planeModeAngle = 0f
    }
}