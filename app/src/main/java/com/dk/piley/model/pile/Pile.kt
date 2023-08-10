package com.dk.piley.model.pile

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dk.piley.model.user.PileMode
import org.threeten.bp.Instant

@Entity
data class Pile(
    @PrimaryKey(autoGenerate = true) val pileId: Long = 0,
    val name: String = "",
    val description: String = "",
    val pileMode: PileMode = PileMode.FREE,
    val pileLimit: Int = 0,
    val createdAt: Instant = Instant.now(),
    val modifiedAt: Instant = Instant.now(),
)
