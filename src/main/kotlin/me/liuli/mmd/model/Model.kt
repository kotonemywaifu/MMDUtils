package me.liuli.mmd.model

import me.liuli.mmd.model.addition.Material
import me.liuli.mmd.model.addition.SubMesh
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
}