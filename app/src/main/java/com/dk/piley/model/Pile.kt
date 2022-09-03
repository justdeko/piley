package com.dk.piley.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Pile(
    @PrimaryKey(autoGenerate = true) val pileId: Long = 0,
    val name: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
