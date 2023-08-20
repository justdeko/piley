package com.dk.piley.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * User entity representing the user and its preferences
 *
 * @property email user email
 * @property name user name
 * @property password user password
 * @property selectedPileId currently selected pile by the user, represented by its id
 * @property defaultPileId default selected pile by the user, represented by its id
 * @property lastBackup timestamp indicating when the user last uploaded a backup
 * @property lastBackupQuery timestamp indicating when the user last requested a backup
 * @property nightMode night mode setting of the user
 * @property dynamicColorOn whether dynamic color theming should be used within the app
 * @property pileMode default pile mode when creating new piles
 * @property defaultReminderDelay default delay for the task reminder delay action
 * @property defaultBackupFrequency default frequency when performing backups
 * @property autoHideKeyboard whether the keyboard should be hidden after creating a new task
 * @property isOffline whether the user is in offline mode
 * @property loadBackupAfterDays setting after how many days the backup should be loaded
 */
@Entity
data class User(
    @PrimaryKey val email: String = "",
    val name: String = "",
    val password: String = "",
    val selectedPileId: Long = 1,
    val defaultPileId: Long = 1,
    val lastBackup: Instant? = null,
    val lastBackupQuery: Instant? = null,
    // user preferences
    val nightMode: NightMode = NightMode.SYSTEM,
    val dynamicColorOn: Boolean = true,
    val pileMode: PileMode = PileMode.FREE,
    val defaultReminderDelay: Int = 15,
    val defaultBackupFrequency: Int = 2,
    val autoHideKeyboard: Boolean = true,
    val isOffline: Boolean = false,
    val loadBackupAfterDays: Int = 1,
)
