package org.example.countdownmanagerapi

import com.mongodb.async.client.MongoCollection
import io.ktor.auth.UserIdPrincipal
import org.bson.conversions.Bson
import org.example.countdownmanagerapi.data.CrudRepository
import org.example.countdownmanagerapi.data.MetaParam
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.eq
import java.lang.Exception

class CountdownRepository(
    override val collection: MongoCollection<Countdown>
): CrudRepository<Countdown> {

    var owner: User? = null
    private val ownerFilter: Bson
        get() = Countdown::owner eq owner?.email

    suspend fun checkCall(principal: UserIdPrincipal?, cds: List<Countdown> = listOf(), fn: suspend (UserIdPrincipal) -> Unit) {
        print("\n\n${cds.joinToString(", ")}\n\n")
        when {
            cds.any { it.owner != owner?.email } -> error("You can't modify other user's countdowns")
            principal == null -> error("No Principal")
            owner == null ->
                throw InvalidCredentialsException("Unauthorized, please log in to continue")
            principal.name != owner?.email ->
                throw InvalidCredentialsException("Session invalid, please log in to continue")
            else -> fn(principal)
        }
    }

    override suspend fun insert(vararg item: Countdown): Boolean {
        return try {
            if (item.size > 1) collection.insertMany(item.toMutableList())
            else collection.insertOne(item[0])
            true
        } catch(e: Exception) {
            false
        }
    }

    override suspend fun update(vararg item: Countdown): Boolean {
        return try {
            for (i in item) collection.updateOne(i)
//            if (item.size > 1) collection.updateMany(item.toMutableList())
//            else collection.updateOne(item[0])
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun delete(vararg item: Countdown): Boolean {
        return try {
            for (i in item) collection.deleteOne(ownerFilter, Countdown::id eq i.id)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun find(vararg filter: Bson?): Countdown? {
        return collection.findOne(ownerFilter, *filter)
    }

    override suspend fun findMany(vararg filter: Bson?): List<Countdown?> {
        return collection.find(ownerFilter, *filter).toList()
    }

    override suspend fun getMeta(vararg params: MetaParam): Map<String, Any> {
        return mapOf(
            *params.map {
                when (it) {
                    MetaParam.LENGTH -> "length" to findMany(ownerFilter).toList().size
                }
            }.toTypedArray()
        )
    }

    override fun getAll(): List<Countdown?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNext(): Countdown? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}