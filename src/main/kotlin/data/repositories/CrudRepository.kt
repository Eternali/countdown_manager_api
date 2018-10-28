package org.example.countdownmanagerapi.data

import com.mongodb.async.client.MongoCollection

interface CrudRepository<T> {

    val collection: MongoCollection<T>

    fun save(item: T): Boolean
    fun delete(item: T): Boolean
    fun find(param: Unit): T
    fun getAll(): List<T>
    fun getNext(): T

}