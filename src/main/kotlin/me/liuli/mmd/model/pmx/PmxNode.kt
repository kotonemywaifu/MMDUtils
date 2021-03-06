package me.liuli.mmd.model.pmx

import me.liuli.mmd.model.addition.IKSolver
import me.liuli.mmd.model.addition.Node
import me.liuli.mmd.utils.slerp
import me.liuli.mmd.utils.vector.instance.Quat
import me.liuli.mmd.utils.vector.mat4f
import me.liuli.mmd.utils.vector.operator.times
import me.liuli.mmd.utils.vector.scale
import me.liuli.mmd.utils.vector.translate
import me.liuli.mmd.utils.vector.vec3f
import javax.vecmath.Vector3f

class PmxNode : Node() {
    var deformDepth = 0
    var deformAfterPhysics = false
    var isAppendRotate = false
    var isAppendTranslate = false
    val appendTranslate = Vector3f(0f, 0f, 0f)
    val appendRotate = Quat(1f, 0f, 0f, 0f)
    var appendNode: PmxNode? = null
    var isAppendLocal = false
    var appendWeight = 0f
    var solver: IKSolver? = null

    override fun beginUpdateTransform() {
        super.beginUpdateTransform()
        appendTranslate.set(0f, 0f, 0f)
        appendRotate.set(0f, 0f, 0f, 1f)
    }

    override fun updateLocalTransform() {
        val t = animateTranslate()
        if(isAppendTranslate) {
            t.add(appendTranslate)
        }

        val r = animateRotate()
        if(enableIk) {
            r.set(r * ikRotate)
        }
        if(isAppendRotate) {
            r.set(r * appendRotate)
        }

        local.set(mat4f(1f).translate(t) * r.castToMat4f() * mat4f(1f).scale(this.scale))
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
                appendRotate = appendNode!!.ikRotate * appendRotate
            }
            this.appendRotate.set(slerp(Quat(1f, 0f, 0f, 0f), appendRotate, appendWeight))
        }

        if(isAppendTranslate) {
            this.appendTranslate.set(if(isAppendLocal) {
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
            } * vec3f(appendWeight))
        }

        updateLocalTransform()
    }
}