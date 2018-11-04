package org.example.countdownmanagerapi

import com.mongodb.async.client.MongoCollection
import org.bson.conversions.Bson
import org.example.countdownmanagerapi.data.CrudRepository
import org.example.countdownmanagerapi.data.MetaParam
import org.litote.kmongo.coroutine.findOne
import org.litote.kmongo.coroutine.insertMany
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.eq

class UserRepository(override val collection: MongoCollection<User>): CrudRepository<User> {

    suspend fun getOrCreate(creds: LoginRegister): User {
        if (creds.email == null || creds.hashedPassword == null)
            throw InvalidCredentialsException("Email and a hashedPassword must be provided")
        var user: User? = find(User::email eq creds.email)
        if (user == null) {
            user = User(creds.email, creds.hashedPassword)
            insert(user)
        } else if (!user.checkPassword(creds.hashedPassword)) {
            throw InvalidCredentialsException("Invalid credentials")
        }
        return user
    }

    override suspend fun insert(vararg item: User): Boolean {
        return try {
            if (item.size > 1) collection.insertOne(item[0])
            else collection.insertMany(item.toMutableList())
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun update(vararg item: User): Boolean {
        return false
    }

    override suspend fun delete(vararg item: User): Boolean {
        return false
    }

    override suspend fun find(vararg filter: Bson?): User? {
        return collection.findOne(*filter)
    }

    override suspend fun findMany(vararg filter: Bson?): List<User> {
        return listOf()
    }

    override suspend fun getMeta(vararg params: MetaParam): Map<String, Any> {
        return mapOf()
    }

    override fun getAll(): List<User?> {
        return listOf()
    }

    override fun getNext(): User? {
        return null
    }

}