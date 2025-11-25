package org.itmo.config

import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Properties

@Configuration
class CoreNLPConfig {

    @Bean
    fun StanfordCoreNLP() : StanfordCoreNLP  = StanfordCoreNLP(
        Properties().apply {
            setProperty("annotators", "tokenize, ssplit, pos, parse, lemma, ner, sentiment")
        }
    )

}