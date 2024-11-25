package com.dk.piley.model.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dk.piley.reminder.DelayRange

/**
 * User entity representing the user and its preferences
 *
 * @property email user email
 * @property name user name
 * @property selectedPileId currently selected pile by the user, represented by its id
 * @property defaultPileId default selected pile by the user, represented by its id
 * @property nightMode night mode setting of the user
 * @property dynamicColorOn whether dynamic color theming should be used within the app
 * @property pileMode default pile mode when creating new piles
 * @property autoHideKeyboard whether the keyboard should be hidden after creating a new task
 * @property showRecurringTasks whether to show recurring tasks by default
 * @property defaultReminderDelayRange the default delay range (minutes, hours, etc.)
 * @property defaultReminderDelayIndex the default delay index for the given range
 */
@Entity
data class User(
    @PrimaryKey val email: String = "",
    val name: String = "",
    val selectedPileId: Long = 1,
    val defaultPileId: Long = 1,
    // user preferences
    val nightMode: NightMode = NightMode.SYSTEM,
    val dynamicColorOn: Boolean = true,
    val pileMode: PileMode = PileMode.FREE,
    val autoHideKeyboard: Boolean = true,
    @ColumnInfo(defaultValue = "0")
    val showRecurringTasks: Boolean = false,
    // reminder delay prefs
    @ColumnInfo(defaultValue = "Minute")
    val defaultReminderDelayRange: DelayRange = DelayRange.Minute,
    @ColumnInfo(defaultValue = "0")
    val defaultReminderDelayIndex: Int = 0,
)
