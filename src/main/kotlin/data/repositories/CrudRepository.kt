package org.example.CountdownManagerApi.data

import org.litote.kmongo.*

interface CrudRepository<T> {

    private val collection

    fun save(item: T): Boolean
    fun delete(item: T): Boolean
    fun find(param: Unit): T
    fun getAll(): List<T>
    fun getNext(): T

}