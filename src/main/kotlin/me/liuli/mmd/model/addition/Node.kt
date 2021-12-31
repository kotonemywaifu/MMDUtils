package me.liuli.mmd.model.addition

import me.liuli.mmd.utils.inverse
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
        inverseInit = (global.clone() as Matrix4f).inverse()
    }

    fun updateGlobalTransform() {
        if(parent == null) {
            global = local.clone() as Matrix4f
        } else {
            global = (parent!!.global.clone() as Matrix4f).apply { mul(local) }
        }
        if(child != null) {
            child!!.updateGlobalTransform()
            child = child!!.next
        }
    }

    fun saveInitialTRS() {
        initTranslate = translate.clone() as Vector3f
        initRotate = rotate.clone() as Vector4f
        initScale = scale.clone() as Vector3f
    }

    fun updateChildTransform() {
        child ?: return
        child!!.updateChildTransform()
        child = child!!.next
    }
}