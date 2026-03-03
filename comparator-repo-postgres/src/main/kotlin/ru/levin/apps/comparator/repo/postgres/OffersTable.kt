package ru.levin.apps.comparator.repo.postgres

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object OffersTable : Table("offers") {
    val id = varchar("id", 64)
    val productId = varchar("product_id", 64)
        .references(ProductsTable.id, onDelete = ReferenceOption.CASCADE)
    val shopName = varchar("shop_name", 256)
    val price = double("price")
    val url = varchar("url", 512)
    override val primaryKey = PrimaryKey(id)
}