package me.liuli.mmd.model.addition

import me.liuli.mmd.utils.vector.inverse
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

    val translate = Vector3f()
    val rotate = Vector4f()
    val scale = Vector3f(1f, 1f, 1f)
    var local = Matrix4f()
    var global = Matrix4f()
    var inverseInit = Matrix4f()

    var initTranslate = Vector3f()
        private set
    var initRotate = Vector4f()
        private set
    var initScale = Vector3f()
        private set
    var enableIk = false

    var animTranslate = Vector3f()
    var animRotate = Vector4f()
    var baseAnimTranslate = Vector3f()
    var baseAnimRotate = Vector4f()
    var ikRotate = Vector4f()

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
        inverseInit = Matrix4f(global).inverse()
    }

    fun updateGlobalTransform() {
        global = if(parent == null) {
            Matrix4f(local)
        } else {
            parent!!.global * local
        }
        if(child != null) {
            child!!.updateGlobalTransform()
            child = child!!.next
        }
    }

    fun saveInitialTRS() {
        initTranslate = Vector3f(translate)
        initRotate = Vector4f(rotate)
        initScale = Vector3f(scale)
    }

    fun updateChildTransform() {
        child ?: return
        child!!.updateChildTransform()
        child = child!!.next
    }

    open fun beginUpdateTransform() {}
    open fun endUpdateTransform() {}
    open fun updateLocalTransform() {}

    fun clearBaseAnimation() {
        baseAnimTranslate = Vector3f()
        baseAnimRotate = Vector4f(0f, 0f, 0f, 1f)
    }

    protected fun animateTranslate(): Vector3f {
        return Vector3f(animTranslate).apply { add(translate) }
    }

    protected fun animateRotate(): Vector4f {
        return animRotate * rotate
    }

    fun loadBaseAnimation() {
        animTranslate = Vector3f(baseAnimTranslate)
        animRotate = Vector4f(baseAnimRotate)
    }

    fun saveBaseAnimation() {
        baseAnimTranslate = Vector3f(animTranslate)
        baseAnimRotate = Vector4f(animRotate)
    }
}