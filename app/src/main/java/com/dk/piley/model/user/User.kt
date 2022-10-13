package com.dk.piley.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val name: String?,
    val selectedPileId: Long = 1,
)
