package me.liuli.mmd.model

import me.liuli.mmd.model.addition.*
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

abstract class Model {

    open val positions = mutableListOf<Vector3f>()
    open val normals = mutableListOf<Vector3f>()
    open val uvs = mutableListOf<Vector2f>()

    open val updatePositions = mutableListOf<Vector3f>()
    open val updateNormals = mutableListOf<Vector3f>()
    open val updateUvs = mutableListOf<Vector2f>()

    open val indices = mutableListOf<Int>()

    open val materials = mutableListOf<Material>()
    open val subMeshes = mutableListOf<SubMesh>()

    abstract fun resetPhysics()

    abstract fun initAnimation()
    abstract fun beginAnimation()
    abstract fun endAnimation()

    open fun saveBaseAnimation() {
        nodes.forEach { it.saveBaseAnimation() }
        morphs.forEach { it.saveBaseAnimation() }
        ikSolvers.forEach { it.saveBaseAnimation() }
    }

    open fun loadBaseAnimation() {
        nodes.forEach { it.loadBaseAnimation() }
        morphs.forEach { it.loadBaseAnimation() }
        ikSolvers.forEach { it.loadBaseAnimation() }
    }

    open fun clearBaseAnimation() {
        nodes.forEach { it.clearBaseAnimation() }
        morphs.forEach { it.clearBaseAnimation() }
        ikSolvers.forEach { it.clearBaseAnimation() }
    }

    abstract fun update()
    abstract fun updateMorphAnimation()
    abstract fun updateNodeAnimation(afterPhysicsAnim: Boolean)
    abstract fun updatePhysicsAnimation(elapsed: Float)

    open fun updateAllAnimation(elapsed: Float) {
        updateMorphAnimation()
        updateNodeAnimation(false)
        updatePhysicsAnimation(elapsed)
        updateNodeAnimation(true)
    }

    protected abstract val nodes: List<Node>
    protected abstract val morphs: List<Morph>
    protected abstract val ikSolvers: List<IKSolver>
}