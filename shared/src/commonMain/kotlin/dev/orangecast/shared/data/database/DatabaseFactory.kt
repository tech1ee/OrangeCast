package dev.orangecast.shared.data.database

import dev.orangecast.shared.database.OrangeCastDatabase

expect class DatabaseFactory {
    fun createDatabase(): OrangeCastDatabase
}