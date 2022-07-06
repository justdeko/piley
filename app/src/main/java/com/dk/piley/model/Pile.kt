package com.dk.piley.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pile(
    @PrimaryKey(autoGenerate = true) val pileId: Long = 0,
    val name: String = "",
    val createdAt: Long = 0,
    val modifiedAt: Long = 0,
)
