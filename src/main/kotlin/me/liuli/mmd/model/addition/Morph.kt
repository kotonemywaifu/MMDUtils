package me.liuli.mmd.model.addition

open class Morph {
    lateinit var name: String
    var weight = 0f
    var saveAnimWeight = 0f

    fun clearBaseAnimation() {
        saveAnimWeight = 0f
    }

    fun saveBaseAnimation() {
        saveAnimWeight = weight
    }

    fun loadBaseAnimation() {
        weight = saveAnimWeight
    }
}