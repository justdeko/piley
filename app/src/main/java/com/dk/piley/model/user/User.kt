package com.dk.piley.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity
data class User(
    @PrimaryKey val email: String = "",
    val name: String = "",
    val password: String = "", // TODO: no password in db
    val selectedPileId: Long = 1,
    val defaultPileId: Long = 1,
    val lastBackup: LocalDateTime? = null,
    // user preferences
    val nightMode: NightMode = NightMode.SYSTEM,
    val dynamicColorOn: Boolean = true,
    val pileMode: PileMode = PileMode.FREE,
    val defaultReminderDelay: Int = 15,
    val defaultBackupFrequency: Int = 2,
    val autoHideKeyboard: Boolean = true,
    val isOffline: Boolean = false
)
