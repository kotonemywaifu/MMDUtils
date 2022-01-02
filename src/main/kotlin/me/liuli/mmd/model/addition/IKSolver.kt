package me.liuli.mmd.model.addition

import me.liuli.mmd.utils.*
import javax.vecmath.Matrix4f
import javax.vecmath.Quat4f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f
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
            val dist = ikPos.dist(targetPos)
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
            val chainIkPos = invChain.mul(Vector4f(ikPos.x, ikPos.y, ikPos.z, 1f)).let { Vector3f(it.x, it.y, it.z) }
            val chainTargetPos = invChain.mul(Vector4f(targetPos.x, targetPos.y, targetPos.z, 1f)).let { Vector3f(it.x, it.y, it.z) }
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
            val rot = Vector4f(angle, cross.x, cross.y, cross.z)
            val chainRot = Vector4f(chainNode.ikRotate).mul(chainNode.animRotate).mul(rot)
            if(chain.enableAxisLimit) {
                val chainRotM = Matrix4f().apply { setRotation(Quat4f(chainRot)) }
                // TODO: Decompose
            }
        }
    }

    fun solvePlane(iterate: Int, index: Int, axis: SolveAxis) {

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
        val saveIKRot = Vector4f(1f, 0f, 0f, 0f)
        var planeModeAngle = 0f
    }
}