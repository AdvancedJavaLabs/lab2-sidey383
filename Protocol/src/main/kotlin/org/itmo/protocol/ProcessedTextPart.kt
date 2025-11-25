package org.itmo.protocol

class ProcessedTextPart(
    val task: String,
    val part: Int,
    val isLast: Boolean,
    val text: String,
    val wordCount: Int,
    val wordCountMap: Map<String, Int>,
    val textTone: Map<String, Int>,
    val sentences: List<Sentence>
)