package org.example.CountdownManagerApi

import org.example.CountdownManagerApi.data.CrudRepository

class UserRepository : CrudRepository<User> {

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