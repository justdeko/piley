package com.dk.piley.model.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dk.piley.ui.reminder.DelayRange
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
 * @property defaultBackupFrequency default frequency when performing backups
 * @property autoHideKeyboard whether the keyboard should be hidden after creating a new task
 * @property isOffline whether the user is in offline mode
 * @property loadBackupAfterDays setting after how many days the backup should be loaded
 * @property showRecurringTasks whether to show recurring tasks by default
 * @property defaultReminderDelayRange the default delay range (minutes, hours, etc.)
 * @property defaultReminderDelayIndex the default delay index for the given range
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
    val defaultBackupFrequency: Int = 2,
    val autoHideKeyboard: Boolean = true,
    val isOffline: Boolean = false,
    val loadBackupAfterDays: Int = 1,
    @ColumnInfo(defaultValue = "0")
    val showRecurringTasks: Boolean = false,
    // reminder delay prefs
    @ColumnInfo(defaultValue = "Minute")
    val defaultReminderDelayRange: DelayRange = DelayRange.Minute,
    @ColumnInfo(defaultValue = "0")
    val defaultReminderDelayIndex: Int = 0
)
