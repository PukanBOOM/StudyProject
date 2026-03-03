package ru.levin.apps.comparator.repo.postgres

import org.jetbrains.exposed.sql.Table

object ProductsTable : Table("products") {
    val id = varchar("id", 64)
    val name = varchar("name", 256)
    val description = text("description")
    val category = varchar("category", 32)
    val lock = varchar("lock", 64)
    override val primaryKey = PrimaryKey(id)
}