package org.itmo

import org.itmo.protocol.ProcessedTextPart
import org.itmo.protocol.Sentence
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class ResultCollector(
    val task: String,
    // Блокировка - если значение есть, значит такое сообщение уже было получено
    val receivedParts: ConcurrentHashMap<Int, Any> = ConcurrentHashMap(),
    val totalCount: AtomicInteger = AtomicInteger(0),
    val worldCount: AtomicInteger = AtomicInteger(0),
    val wordCountMap: ConcurrentHashMap<String, Int> = ConcurrentHashMap(),
    val textTone: ConcurrentHashMap<String, Int> = ConcurrentHashMap(),
    var sentences: ConcurrentHashMap<Int, List<Sentence>> = ConcurrentHashMap(),
    // "Последний рубеж", если значение установлено, значит мета-данные текста полностью сохранены
    val textParts: ConcurrentHashMap<Int, String> = ConcurrentHashMap(),
) {

    /**
     * @return true только если к моменту завершения методы данные всех блоков сохранены
     * **/
    fun consume(part: ProcessedTextPart): Boolean {
        if (!markAsReceived(part.part)) return false
        if (part.isLast) {
            totalCount.set(part.part)
        }
        worldCount.addAndGet(part.wordCount)
        part.wordCountMap.forEach { (k, v) ->
            wordCountMap.compute(k) { _, oldV -> v + (oldV ?: 0) }
        }
        part.textTone.forEach { (k, v) ->
            textTone.compute(k) { _, oldV -> v + (oldV ?: 0) }
        }
        sentences[part.part] = part.sentences
        textParts[part.part] = part.text
        return isComplete()
    }

    private fun markAsReceived(part: Int): Boolean {
        return receivedParts.putIfAbsent(part, Any()) == null
    }

    /**
     * @return true, если данные всех блоков сохранены
     * */
    private fun isComplete(): Boolean {
        // Если нет значения - как минимум последний блок ещё не дошел до isComplete() и обязательно ещё вызовет эту функцию
        if (totalCount.get() == 0) return false
        /**
         * Если ожидаемое количество совпадает с текущим - все части текста уже были обработаны
         *
         * Какой-то из методов consume точно вызовет этот метод т.к. textParts синхронизован и последний
         * положивший в него данные обязательно вызовет этот метод
        **/
        return textParts.size == totalCount.get()
    }

}
