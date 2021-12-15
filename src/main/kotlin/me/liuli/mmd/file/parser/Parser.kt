package me.liuli.mmd.file.parser

import me.liuli.mmd.file.InteractiveFile

interface Parser<T : InteractiveFile> {

    /**
     * 从[ByteArray]读取文件
     *
     * @param input 源文件
     * @return 读取完成的数据
     */
    fun read(input: ByteArray): T

    /**
     * read data to [T]
     */
    fun readToInstance(instance: T, input: ByteArray)

    /**
     * 将数据写入[ByteArray]
     *
     * @param data 数据
     * @return 目标文件
     */
    fun write(data: T): ByteArray
}