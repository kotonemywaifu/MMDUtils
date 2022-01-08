package me.liuli.mmd.model.addition.physics

import com.bulletphysics.linearmath.MotionState
import com.bulletphysics.linearmath.Transform
import me.liuli.mmd.model.addition.Node
import me.liuli.mmd.model.pmx.PmxNode
import me.liuli.mmd.utils.vector.invZ
import me.liuli.mmd.utils.vector.inverse
import me.liuli.mmd.utils.vector.mat4f
import me.liuli.mmd.utils.vector.operator.times
import javax.vecmath.Matrix4f

abstract class MMDMotionState : MotionState() {

    abstract fun reset()

    abstract fun reflectGlobalTransform()
}

class DefaultMotionState(transform: Matrix4f) : MMDMotionState() {

    var transform: Transform
    var initialTransform: Transform

    init {
        val invZMat = Matrix4f(transform).invZ()
        this.transform = Transform(invZMat)
        this.initialTransform = Transform(invZMat)
    }

    override fun getWorldTransform(out: Transform): Transform {
        return transform
    }

    override fun setWorldTransform(worldTrans: Transform) {
        transform.set(worldTrans)
    }

    override fun reset() {
        transform.set(initialTransform)
    }

    override fun reflectGlobalTransform() {}
}

open class DynamicMotionState(protected val node: Node, protected val offset: Matrix4f) : MMDMotionState() {

    protected val invOffset = Matrix4f(offset).inverse()
    protected lateinit var transform: Transform

    init {
        reset()
    }

    override fun reset() {
        transform = Transform((node.global * offset).invZ())
    }

    override fun reflectGlobalTransform() {
        val world = transform.getMatrix(mat4f(1f))
        val global = world.invZ() * invOffset
        node.global.set(global)
        node.updateChildTransform()
    }

    override fun getWorldTransform(out: Transform): Transform {
        return transform
    }

    override fun setWorldTransform(worldTrans: Transform) {
        transform.set(worldTrans)
    }
}

class DynamicAndBoneMergeMotionState(node: Node, offset: Matrix4f) : DynamicMotionState(node, offset) {

    override fun reflectGlobalTransform() {
        val world = transform.getMatrix(mat4f(1f))
        val global = world.invZ() * invOffset
        val nGlobal = node.global
        global.m30 = nGlobal.m30
        global.m31 = nGlobal.m31
        global.m32 = nGlobal.m32
        global.m33 = nGlobal.m33
        node.global.set(global)
        node.updateChildTransform()
    }
}

class KinematicMotionState(private val node: PmxNode?, private val offset: Matrix4f) : MMDMotionState() {

    override fun getWorldTransform(out: Transform?): Transform {
        return Transform(if(node == null) {
            Matrix4f(offset)
        } else {
            node.global * offset
        }.invZ())
    }

    override fun setWorldTransform(worldTrans: Transform?) {}

    override fun reset() {}

    override fun reflectGlobalTransform() {}
}