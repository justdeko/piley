package com.dk.piley.model.pile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dk.piley.model.user.PileMode
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Pile entity representing a user pile
 *
 * @property pileId id of the pile (autogenerated)
 * @property name name of the pile
 * @property description pile description
 * @property pileMode pile mode, representing how tasks in that pile can be completed
 * @property pileLimit task limit for the pile
 * @property createdAt represents when the pile was created
 * @property modifiedAt represents when the pile was last modified
 * @property deletedCount number of deleted tasks from this pile
 * @property color color of the pile, none by default
 */
@Entity
data class Pile(
    @PrimaryKey(autoGenerate = true) val pileId: Long = 0,
    val name: String = "",
    val description: String = "",
    val pileMode: PileMode = PileMode.FREE,
    val pileLimit: Int = 0,
    val createdAt: Instant = Clock.System.now(),
    val modifiedAt: Instant = Clock.System.now(),
    @ColumnInfo(defaultValue = "0")
    val deletedCount: Int = 0,
    @ColumnInfo(defaultValue = "")
    val color: PileColor = PileColor.NONE
)
