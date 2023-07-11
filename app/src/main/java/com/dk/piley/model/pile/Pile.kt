package com.dk.piley.model.pile

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dk.piley.model.user.PileMode
import com.dk.piley.util.utcZoneId
import org.threeten.bp.LocalDateTime

@Entity
data class Pile(
    @PrimaryKey(autoGenerate = true) val pileId: Long = 0,
    val name: String = "",
    val description: String = "",
    val pileMode: PileMode = PileMode.FREE,
    val pileLimit: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(utcZoneId),
    val modifiedAt: LocalDateTime = LocalDateTime.now(utcZoneId),
)
