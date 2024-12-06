package com.dk.piley.ui.pile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.ui.piles.PileStatistics
import com.dk.piley.ui.profile.UpcomingTasksSection
import com.dk.piley.util.MediumSpacer
import com.dk.piley.util.getCompletedTasksForWeekValues
import com.dk.piley.util.getUpcomingTasks

/**
 * Supporting pile detail screen that shows up on the pile screen when user is on a large screen
 *
 * @param pile pile with tasks to calculate statistics and show upcoming tasks
 */
@Composable
fun SupportingPileDetailScreen(
    pile: PileWithTasks,
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        PileStatistics(
            doneCount = pile.tasks.count { it.status == TaskStatus.DONE },
            deletedCount = pile.pile.deletedCount,
            currentCount = pile.tasks.count { it.status == TaskStatus.DEFAULT },
            completedTaskCounts = getCompletedTasksForWeekValues(pile),
            clearStatisticsVisible = false,
        )
        MediumSpacer()
        UpcomingTasksSection(
            modifier = Modifier.fillMaxWidth(),
            pileNameTaskList = getUpcomingTasks(listOf(pile))
        )
    }
}