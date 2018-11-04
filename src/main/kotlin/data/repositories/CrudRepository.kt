package org.example.countdownmanagerapi.data

import com.mongodb.async.client.MongoCollection
import io.ktor.http.HttpMethod
import org.bson.conversions.Bson

enum class MetaParam {
    LENGTH
}

interface CrudRepository<T> {

    val collection: MongoCollection<T>

    suspend fun insert(vararg item: T): Boolean
    suspend fun update(vararg item: T): Boolean
    suspend fun delete(vararg item: T): Boolean
    suspend fun find(vararg filter: Bson?): T?
    suspend fun findMany(vararg filter: Bson?): List<T?>
    suspend fun getMeta(vararg params: MetaParam): Map<String, Any>
    fun getAll(): List<T?>
    fun getNext(): T?

}