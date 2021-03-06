package org.alex34567.chess

import kotlin.math.abs

private fun <T> genPool(constructor: (Int) -> T): List<T> {
    val pool: MutableList<T> = ArrayList(8)

    for (x in 0..7) {
        pool.add(constructor(x))
    }

    return pool
}

abstract class RankFileFactory<out T> internal constructor() {
    abstract fun newInstance(rankFile: Int): T?
}

sealed class RankFile<T : RankFile<T>> constructor(protected val rawRankFile: Int) : Comparable<T> {

    abstract val factory: RankFileFactory<T>
    abstract fun toInt(): Int

    abstract override fun toString(): String

    abstract override fun equals(other: Any?): Boolean

    override final fun hashCode(): Int {
        return rawRankFile.hashCode()
    }

    operator fun plus(other: T): T? {
        return factory.newInstance(rawRankFile + other.toInt())
    }

    operator fun minus(other: T): T? {
        return factory.newInstance(rawRankFile - other.toInt())
    }

    fun absSub(other: T): Int {
        val thisInt = toInt()
        val otherInt = other.toInt()
        return abs(thisInt - otherInt)
    }

    override final operator fun compareTo(other: T): Int {
        if (rawRankFile < other.toInt()) return -1

        if (rawRankFile > other.toInt()) return 1

        return 0
    }

    operator fun rangeTo(other: T): Iterable<T> {
        //Handle backwards case
        if (this >= other) return emptyList()

        //Ignore null because other cannot be out of bounds
        return (rawRankFile..other.toInt()).map({ factory.newInstance(it)!! })
    }
}

class Rank private constructor(rank: Int) : RankFile<Rank>(rank) {
    override fun toString(): String {
        return Integer.toString(toInt())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Rank) return false

        if (toInt() == other.toInt()) return true

        return false
    }

    override fun toInt(): Int {
        return rawRankFile + 1
    }

    override val factory: RankFileFactory<Rank> get() = Factory

    companion object Factory : RankFileFactory<Rank>() {
        private val POOL = genPool { rank -> Rank(rank) }

        val ONE get() = POOL[0]
        val TWO get() = POOL[1]
        val THREE get() = POOL[2]
        val FOUR get() = POOL[3]
        val FIVE get() = POOL[4]
        val SIX get() = POOL[5]
        val SEVEN get() = POOL[6]
        val EIGHT get() = POOL[7]

        override fun newInstance(rankFile: Int): Rank? {
            if (rankFile < 1 || rankFile > 8) return null

            return POOL[rankFile - 1]
        }

    }
}

class File private constructor(file: Int) : RankFile<File>(file) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is File) return false

        if (toInt() == other.toInt()) return true

        return false
    }

    override fun toInt(): Int {
        return rawRankFile
    }

    override fun toString(): String {
        return ('A' + toInt()).toString()
    }

    override val factory: RankFileFactory<File> get() = Factory

    companion object Factory : RankFileFactory<File>() {
        private val POOL = genPool { file -> File(file) }

        val A get() = POOL[0]
        val B get() = POOL[1]
        val C get() = POOL[2]
        val D get() = POOL[3]
        val E get() = POOL[4]
        val F get() = POOL[5]
        val G get() = POOL[6]
        val H get() = POOL[7]

        override fun newInstance(rankFile: Int): File? {
            if (rankFile < 0 || rankFile > 7) return null

            return POOL[rankFile]
        }
    }
}
