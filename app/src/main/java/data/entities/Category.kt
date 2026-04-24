package data.entities

import androidx.room.Entity//the imports were needed once removed error ensues
import androidx.room.PrimaryKey
//each expense belongs to a category are we talking about groceries or transport

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,           // Which user this belongs to
    val name: String,          // e.g. "Groceries", "Transport"
    val iconName: String = "", // For displaying an icon
    val colorHex: String = "#FF0000"
)