package me.liuli.mmd.model.addition

import me.liuli.mmd.utils.vector.*
import me.liuli.mmd.utils.vector.operator.plus
import me.liuli.mmd.utils.vector.operator.times
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

open class Node {
    var name: String = ""
    var child: Node? = null
    var parent: Node? = null
    var next: Node? = null
    var prev: Node? = null

    val translate = Vector3f(0f, 0f, 0f)
    val rotate = Vector4f(0f, 0f, 0f, 1f)
    val scale = Vector3f(1f, 1f, 1f)
    val local = mat4f(1f)
    val global = mat4f(1f)
    val inverseInit = mat4f(1f)

    val initTranslate = Vector3f(0f, 0f, 0f)
    val initRotate = Vector4f(0f, 0f, 0f, 1f)
    val initScale = Vector3f(1f, 1f, 1f)
    var enableIk = false

    val animTranslate = Vector3f(0f, 0f, 0f)
    val animRotate = Vector4f(0f, 0f, 0f, 1f)
    val baseAnimTranslate = Vector3f(0f, 0f, 0f)
    val baseAnimRotate = Vector4f(0f, 0f, 0f, 1f)
    val ikRotate = Vector4f(0f, 0f, 0f, 1f)

    fun addChild(node: Node) {
        node.parent = this
        if(child == null) {
            child = node
            child!!.next = null
            child!!.prev = child
        } else {
            val lastNode = child!!.prev
            lastNode!!.next = node
            node.prev = lastNode
            child!!.prev = node
        }
    }

    fun calculateInverseInitTransform() {
        inverseInit.set(Matrix4f(global).inverse())
    }

    fun updateGlobalTransform() {
        global.set(if(parent == null) {
            local
        } else {
            parent!!.global * local
        })
        if(child != null) {
            child!!.updateGlobalTransform()
            child = child!!.next
        }
    }

    fun saveInitialTRS() {
        initTranslate.set(translate)
        initRotate.set(rotate)
        initScale.set(scale)
    }

    fun updateChildTransform() {
        child ?: return
        child!!.updateGlobalTransform()
        child = child!!.next
    }

    open fun beginUpdateTransform() {
        translate.set(initTranslate)
        rotate.set(initRotate)
        scale.set(initScale)
        ikRotate.set(0f, 0f, 0f, 1f)
    }

    open fun endUpdateTransform() {}

    open fun updateLocalTransform() {
        val s = mat4f(1f).scale(scale)
        val r = animateRotate().castToMat4f()
        val t = mat4f(1f).translate(translate)
        if(enableIk) {
            r.mul(ikRotate.castToMat4f())
        }
        local.set(t * r * s)
    }

    fun clearBaseAnimation() {
        baseAnimTranslate.set(0f, 0f, 0f)
        baseAnimRotate.set(0f, 0f, 0f, 1f)
    }

    protected fun animateTranslate(): Vector3f {
        return animTranslate + translate
    }

    protected fun animateRotate(): Vector4f {
        return animRotate * rotate
    }

    fun loadBaseAnimation() {
        animTranslate.set(baseAnimTranslate)
        animRotate.set(baseAnimRotate)
    }

    fun saveBaseAnimation() {
        baseAnimTranslate.set(animTranslate)
        baseAnimRotate.set(animRotate)
    }
}