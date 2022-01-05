package me.liuli.mmd.model.pmx

import me.liuli.mmd.file.PmxFile
import me.liuli.mmd.utils.mix
import me.liuli.mmd.utils.vector.operator.plus
import me.liuli.mmd.utils.vector.operator.times
import me.liuli.mmd.utils.vector.vec3f
import me.liuli.mmd.utils.vector.vec4f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

class PmxMaterialFactor(val diffuse: Vector3f, var alpha: Float,
                        val specular: Vector3f,  var specularPower: Float,
                        val ambient: Vector3f, var edgeColor: Vector4f,
                        var edgeSize: Float, val textureFactor: Vector4f,
                        val spTextureFactor: Vector4f, val toonTextureFactor: Vector4f) {

    fun mul(mf: PmxMaterialFactor, weight: Float) {
        diffuse.set(mix(diffuse, diffuse * mf.diffuse, weight))
        alpha = mix(alpha, alpha * mf.alpha, weight)
        specular.set(mix(specular, specular * mf.specular, weight))
        specularPower = mix(specularPower, specularPower * mf.specularPower, weight)
        ambient.set(mix(ambient, ambient * mf.ambient, weight))
        edgeColor.set(mix(edgeColor, edgeColor * mf.edgeColor, weight))
        edgeSize = mix(edgeSize, edgeSize * mf.edgeSize, weight)
        textureFactor.set(mix(textureFactor, textureFactor * mf.textureFactor, weight))
        spTextureFactor.set(mix(spTextureFactor, spTextureFactor * mf.spTextureFactor, weight))
        toonTextureFactor.set(mix(toonTextureFactor, toonTextureFactor * mf.toonTextureFactor, weight))
    }

    fun add(mf: PmxMaterialFactor, weight: Float) {
        diffuse.set(diffuse + (mf.diffuse * weight))
        alpha += mf.alpha * weight
        specular.set(specular + (mf.specular * weight))
        specularPower += mf.specularPower * weight
        ambient.set(ambient + (mf.ambient * weight))
        edgeColor.set(edgeColor + (mf.edgeColor * weight))
        edgeSize += mf.edgeSize * weight
        textureFactor.set(textureFactor + (mf.textureFactor * weight))
        spTextureFactor.set(spTextureFactor + (mf.spTextureFactor * weight))
        toonTextureFactor.set(toonTextureFactor + (mf.toonTextureFactor * weight))
    }

    companion object {
        fun mat(pmxMat: PmxFile.Morph.MaterialOffset): PmxMaterialFactor {
            return PmxMaterialFactor(Vector3f(pmxMat.diffuse.x, pmxMat.diffuse.y, pmxMat.diffuse.z), pmxMat.diffuse.w,
                Vector3f(pmxMat.specular), pmxMat.specularlity,
                Vector3f(pmxMat.ambient), Vector4f(pmxMat.edgeColor.red / 255f, pmxMat.edgeColor.green / 255f, pmxMat.edgeColor.blue / 255f, pmxMat.edgeColor.alpha / 255f),
                pmxMat.edgeSize, Vector4f(pmxMat.textureFactor), Vector4f(pmxMat.sphereTextureFactor), Vector4f(pmxMat.toonTextureFactor)
            )
        }

        fun initAdd(): PmxMaterialFactor {
            return PmxMaterialFactor(vec3f(0f), 0f, vec3f(0f), 0f,
                vec3f(0f), vec4f(0f), 0f, vec4f(0f), vec4f(0f), vec4f(0f))
        }

        fun initMul(): PmxMaterialFactor {
            return PmxMaterialFactor(vec3f(1f), 1f, vec3f(1f), 1f,
                vec3f(1f), vec4f(0f), 1f, vec4f(1f), vec4f(1f), vec4f(1f))
        }
    }
}