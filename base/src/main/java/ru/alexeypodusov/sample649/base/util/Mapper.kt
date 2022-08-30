package ru.alexeypodusov.sample649.base.util

interface Mapper<SRC, DST> {
    fun transform(data: SRC): DST
}