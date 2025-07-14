package com.dk.piley.reminder

import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.util.toLocalDateTimeMinutes
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

class ReminderObserver(
    private val notificationManager: INotificationManager,
    private val taskRepository: TaskRepository,
    private val pileRepository: PileRepository,
) {
    suspend fun checkCurrentReminders() {
        val tasks = taskRepository.getTasks().first()
        tasks.filter {
            it.reminder != null && it.reminder.toLocalDateTimeMinutes() == Clock.System.now()
                .toLocalDateTimeMinutes()
        }.forEach {
            val pileName = pileRepository.getPileById(it.pileId).first().pile.name
            notificationManager.showNotification(it, pileName)
        }
    }
}