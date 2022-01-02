package me.liuli.mmd.model.pmx

import me.liuli.mmd.model.addition.IKSolver
import me.liuli.mmd.model.addition.Node
import me.liuli.mmd.utils.*
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

class PmxNode : Node() {
    var deformDepth = 0
    var deformAfterPhysics = false
    var isAppendRotate = false
    var isAppendTranslate = false
    var appendTranslate = Vector3f()
    var appendRotate = Vector4f()
    var appendNode: PmxNode? = null
    var isAppendLocal = false
    var appendWeight = 0f
    var solver: IKSolver? = null

    override fun beginUpdateTransform() {
        appendTranslate = Vector3f()
        appendRotate = Vector4f(1f, 0f, 0f, 0f)
    }

    override fun updateLocalTransform() {
        val t = animateTranslate()
        if(isAppendTranslate) {
            t.add(appendTranslate)
        }

        val r = animateRotate()
        if(enableIk) {
            r.mul(ikRotate)
        }
        if(isAppendRotate) {
            r.mul(appendRotate)
        }

        local = mat4f(1f).translate(t).apply { mul(r.castToMat4f()) }.also { it.mul(mat4f(1f).scale(this.scale)) }
    }

    fun updateAppendTransform() {
        if(appendNode == null) return

        if(isAppendRotate) {
            var appendRotate = if(isAppendLocal) {
                appendNode!!.animateRotate()
            } else {
                if(appendNode!!.appendNode != null) {
                    appendNode!!.appendNode!!.animateRotate()
                } else {
                    appendNode!!.animateRotate()
                }
            }
            if(appendNode!!.enableIk) {
                appendRotate = Vector4f(appendNode!!.ikRotate).mul(appendRotate)
            }
            this.appendRotate = slerp(Vector4f(1f, 0f, 0f, 0f), appendRotate, appendWeight)
        }

        if(isAppendTranslate) {
            this.appendTranslate = if(isAppendLocal) {
                Vector3f(appendNode!!.translate).also {
                    it.x -= appendNode!!.initTranslate.x
                    it.y -= appendNode!!.initTranslate.y
                    it.z -= appendNode!!.initTranslate.z
                }
            } else {
                if(appendNode!!.appendNode != null) {
                    Vector3f(appendNode!!.appendNode!!.translate)
                } else {
                    Vector3f(appendNode!!.translate).also {
                        it.x -= appendNode!!.initTranslate.x
                        it.y -= appendNode!!.initTranslate.y
                        it.z -= appendNode!!.initTranslate.z
                    }
                }
            }.mul(appendWeight, appendWeight, appendWeight)
        }

        updateLocalTransform()
    }
}