package com.bossmg.android.domain.mapper


interface Mapper<in I, out O> {
    fun map(input: I): O
}

interface BiMapper<I, O> : Mapper<I, O> {
    fun mapBack(output: O): I
}