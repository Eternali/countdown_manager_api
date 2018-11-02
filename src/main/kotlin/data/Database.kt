package org.example.countdownmanagerapi

import com.mongodb.async.client.MongoClient
import com.mongodb.async.client.MongoDatabase
import org.litote.kmongo.async.KMongo

class Database(val name: String) {

    private var client: MongoClient = KMongo.createClient()
    var db: MongoDatabase

    init {
        db = client.getDatabase(name)
    }

    fun close() {
        client.close()
    }

    companion object {

        private var instances: MutableList<Database> = mutableListOf()

        fun getInstance(name: String): Database {
            val database: Database?
            if (!instances.asSequence().map { it.name }.contains(name)) {
                database = Database(name)
                instances.add(database)
            } else {
                database = instances.filter { it.name == name }[0]
            }
            return database
        }

        fun getDatabase(name: String): MongoDatabase = getInstance(name).db

        fun destroy(name: String) {
            val instance = instances.filter { it.name == name }[0]
            instance.close()
            instances.remove(instance)
        }

        fun destroyAll() {
            instances.forEach { it.close() }
        }
    }

}
