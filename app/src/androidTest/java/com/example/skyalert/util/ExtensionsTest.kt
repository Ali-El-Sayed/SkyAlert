package com.example.skyalert.util

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ExtensionsTest {

    @Test
    fun toCapitalizedWords_convertStringToCapitalizedWords_returnStringWithCapitalizedWords() {
        // given
        val input = "hello world"
        val expected = "Hello World"

        // when
        val result = input.toCapitalizedWords()

        // then
        assertThat(result, `is`(expected))
    }
}