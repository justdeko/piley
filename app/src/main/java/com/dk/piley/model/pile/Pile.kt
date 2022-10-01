package com.dk.piley.model.pile

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dk.piley.ui.util.utcZoneId
import org.threeten.bp.LocalDateTime

@Entity
data class Pile(
    @PrimaryKey(autoGenerate = true) val pileId: Long = 0,
    val name: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(utcZoneId),
    val modifiedAt: LocalDateTime = LocalDateTime.now(utcZoneId),
)
