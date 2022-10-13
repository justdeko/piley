package com.dk.piley.model.user

import androidx.room.Embedded
import androidx.room.Relation
import com.dk.piley.model.pile.Pile

data class UserWithPiles(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "pileId"
    )
    val piles: List<Pile>
)