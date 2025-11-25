package org.itmo.protocol

class TextPart(
    val task: String,
    val part: Int,
    val namePlaceholder: String,
    val isLast: Boolean,
    val text: String,
)