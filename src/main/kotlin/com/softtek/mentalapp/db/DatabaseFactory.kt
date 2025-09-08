package com.softtek.mentalapp.db

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase

object DatabaseFactory {
    private lateinit var client: MongoClient
    lateinit var database: MongoDatabase

    fun init() {
        val connectionString =
            ConnectionString("mongodb://root:rootpassword@localhost:27017/mentalapp?authSource=admin")
        val settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()

        client = MongoClients.create(settings)
        database = client.getDatabase("mentalapp")
        println("MongoDB conectado com sucesso!")
    }
}


