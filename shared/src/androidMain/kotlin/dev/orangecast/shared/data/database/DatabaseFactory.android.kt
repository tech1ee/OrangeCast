package dev.orangecast.shared.data.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.orangecast.shared.database.OrangeCastDatabase

actual class DatabaseFactory(private val context: Context) {
    actual fun createDatabase(): OrangeCastDatabase {
        val driver: SqlDriver = AndroidSqliteDriver(
            schema = OrangeCastDatabase.Schema,
            context = context,
            name = "orangecast.db"
        )
        return OrangeCastDatabase(driver)
    }
}