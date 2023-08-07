package com.bluetriangle.android.demo

val alphabets = "abcdefghijklmnopqrstuvwxyz"
val interval = 10L
val word = "english"

fun main() {
    val startTime = System.currentTimeMillis()
    val result = arrayListOf<Char>()
    for(c in word) {
        result.add('-')
    }
    val generated = generateWord(word, result.toTypedArray(), 0, startTime, interval)
    println(generated)
}

fun generateWord(
    word: String,
    result:Array<Char>,
    index: Int,
    startTime: Long,
    interval: Long
):Boolean {
    if(index == word.length) return resultEqualsWord(result, word)

    for(alphabet in alphabets) {
        if(System.currentTimeMillis() - startTime > (interval * 1000)) return false
        result[index] = alphabet
        for(r in result) {
            print(r)
        }
        println()
        if(generateWord(word, result, index+1, startTime, interval)) return true
        result[index] = '-'
    }
    return false
}

fun resultEqualsWord(result: Array<Char>, word: String): Boolean {
    for(i in word.indices) {
        if(result[i] != word[i]) return false
    }
    return true
}