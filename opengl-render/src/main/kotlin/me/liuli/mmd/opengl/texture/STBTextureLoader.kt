package me.liuli.mmd.opengl.texture

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.stb.STBImage.*
import org.lwjgl.stb.STBImageResize.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.memAlloc
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt


/**
 * load texture from file with stb library in lwjgl
 */
class STBTextureLoader : ITextureLoader {
    override fun load(textureFile: File): Int {
        val imageBuffer = textureFile.readBytes().let { arr ->
            BufferUtils.createByteBuffer(arr.size).apply {
                put(arr)
                flip()
            }
        }

        val stack = MemoryStack.stackPush()
        val wBuf = stack.mallocInt(1)
        val hBuf = stack.mallocInt(1)
        val compBuf = stack.mallocInt(1)

        // Use info to read image metadata without decoding the entire image.
        // We don't need this for this demo, just testing the API.
        if (!stbi_info_from_memory(imageBuffer, wBuf, hBuf, compBuf)) {
            throw RuntimeException("Failed to read image information: " + stbi_failure_reason())
        }
        // Decode the image
        val image = stbi_load_from_memory(imageBuffer, wBuf, hBuf, compBuf, 0) ?: throw RuntimeException("Failed to load image: " + stbi_failure_reason())
        val w = wBuf[0]
        val h = hBuf[0]
        val comp = compBuf[0]

        val texID: Int = GL11.glGenTextures()

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)

        val format: Int
        if (comp == 3) {
            if (w and 3 != 0) {
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 2 - (w and 1))
            }
            format = GL11.GL_RGB
        } else {
            val stride = w * 4
            for (y in 0 until h) {
                for (x in 0 until w) {
                    val i = y * stride + x * 4
                    val alpha: Float = (image[i + 3].toInt() and 0xFF) / 255.0f
                    image.put(i + 0, ((image[i + 0].toInt() and 0xFF) * alpha).roundToInt().toByte())
                    image.put(i + 1, ((image[i + 1].toInt() and 0xFF) * alpha).roundToInt().toByte())
                    image.put(i + 2, ((image[i + 2].toInt() and 0xFF) * alpha).roundToInt().toByte())
                }
            }
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA)
            format = GL11.GL_RGBA
        }

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, w, h, 0, format, GL11.GL_UNSIGNED_BYTE, image)

        var inputPixels = image
        var inputW = w
        var inputH = h
        var mipmapLevel = 0
        while (1 < inputW || 1 < inputH) {
            val outputW = 1.coerceAtLeast(inputW shr 1)
            val outputH = 1.coerceAtLeast(inputH shr 1)
            val outputPixels = memAlloc(outputW * outputH * comp)
            stbir_resize_uint8_generic(
                inputPixels, inputW, inputH, inputW * comp,
                outputPixels, outputW, outputH, outputW * comp,
                comp, if (comp == 4) 3 else STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
                STBIR_EDGE_CLAMP,
                STBIR_FILTER_MITCHELL,
                STBIR_COLORSPACE_SRGB
            )
            if (mipmapLevel == 0) {
                stbi_image_free(image)
            } else {
                MemoryUtil.memFree(inputPixels)
            }
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, ++mipmapLevel, format, outputW, outputH, 0, format, GL11.GL_UNSIGNED_BYTE, outputPixels)
            inputPixels = outputPixels
            inputW = outputW
            inputH = outputH
        }
        if (mipmapLevel == 0) {
            stbi_image_free(image)
        } else {
            MemoryUtil.memFree(inputPixels)
        }

        return texID
    }

    override fun bind(textureIdentifier: Int) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIdentifier)
    }

    override fun delete(textureIdentifier: Int) {
        GL11.glDeleteTextures(textureIdentifier)
    }
}