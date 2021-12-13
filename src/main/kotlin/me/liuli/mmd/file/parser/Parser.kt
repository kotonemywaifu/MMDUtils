package me.liuli.mmd.file.parser

interface Parser<T> {

    /**
     * 从[ByteArray]读取文件
     *
     * @param input 源文件
     * @return 读取完成的数据
     */
    fun read(input: ByteArray): T

    /**
     * 将数据写入[ByteArray]
     *
     * @param data 数据
     * @return 目标文件
     */
    fun write(data: T): ByteArray
}