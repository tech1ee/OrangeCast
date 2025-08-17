package dev.orangecast.shared.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.orangecast.shared.database.OrangeCastDatabase

actual class DatabaseFactory {
    actual fun createDatabase(): OrangeCastDatabase {
        val driver: SqlDriver = NativeSqliteDriver(
            schema = OrangeCastDatabase.Schema,
            name = "orangecast.db"
        )
        return OrangeCastDatabase(driver)
    }
}