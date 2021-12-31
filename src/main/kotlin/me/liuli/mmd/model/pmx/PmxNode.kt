package me.liuli.mmd.model.pmx

import me.liuli.mmd.model.addition.IKSolver
import me.liuli.mmd.model.addition.Node

class PmxNode : Node() {
    var deformDepth = 0
    var deformAfterPhysics = false
    var appendRotate = false
    var appendTranslate = false
    var appendNode: PmxNode? = null
    var appendLocal = false
    var appendWeight = 0f
    var solver: IKSolver? = null
}