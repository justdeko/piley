package com.dk.piley.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity
data class Pile(
    @PrimaryKey(autoGenerate = true) val pileId: Long = 0,
    val name: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val modifiedAt: LocalDateTime = LocalDateTime.now(),
)
