package org.example.countdownmanagerapi

import com.mongodb.async.client.MongoCollection
import org.example.countdownmanagerapi.data.CrudRepository

class UserRepository(override val collection: MongoCollection<User>): CrudRepository<User> {

    override fun save(item: User): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(item: User): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun find(param: Unit): User {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAll(): List<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNext(): User {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}