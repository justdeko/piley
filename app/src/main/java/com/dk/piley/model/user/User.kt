package com.dk.piley.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "", // TODO: no password in db
    val selectedPileId: Long = 1,
    val defaultPileId: Long = 1,
    // user preferences
    val nightMode: NightMode = NightMode.SYSTEM,
    val dynamicColorOn: Boolean = true,
    val pileMode: PileMode = PileMode.FREE,
    val defaultReminderDelay: Int = 15,
    val autoHideKeyboard: Boolean = true,
)
