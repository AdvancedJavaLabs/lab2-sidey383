package org.itmo.protocol

class Sentence(
    val start: Int,
    val end: Int,
    val length: Int
): Comparable<Sentence> {
    override fun compareTo(other: Sentence): Int = length.compareTo(other.length)
}