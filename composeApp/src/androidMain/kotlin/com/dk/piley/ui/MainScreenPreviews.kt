package com.dk.piley.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.pile.PileScreen
import com.dk.piley.ui.pile.PileViewState
import com.dk.piley.ui.piles.PileDetailScreen
import com.dk.piley.ui.piles.PileDetailViewState
import com.dk.piley.ui.piles.PileOverviewScreen
import com.dk.piley.ui.piles.PilesViewState
import com.dk.piley.ui.profile.ProfileScreen
import com.dk.piley.ui.profile.ProfileViewState
import com.dk.piley.ui.task.TaskDetailScreen
import com.dk.piley.ui.task.TaskDetailViewState
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.bigPreviewPile
import com.dk.piley.util.getPreviewTransitionStates
import com.dk.piley.util.previewPileWithTasksList
import com.dk.piley.util.previewUpcomingTasksList

@PreviewMainScreen
@Composable
private fun PileScreenPreview() {
    PileyTheme {
        Surface {
            val pileWithTasks = bigPreviewPile
            val state = PileViewState(
                pileWithTasks = pileWithTasks,
                tasks = pileWithTasks.tasks,
                pileIdTitleList = listOf(Pair(pileWithTasks.pile.pileId, pileWithTasks.pile.name)),
                showRecurring = true
            )
            PileScreen(
                viewState = state,
                shownTasks = pileWithTasks.tasks.subList(0, 7),
                taskTransitionStates = pileWithTasks.tasks.getPreviewTransitionStates()
            )
        }
    }
}

@PreviewMainScreen
@Composable
private fun PileOverviewScreenPreview() {
    PileyTheme {
        Surface {
            val state = PilesViewState(piles = previewPileWithTasksList)
            PileOverviewScreen(
                viewState = state,
                pileTransitionStates = previewPileWithTasksList.getPreviewTransitionStates()
            )
        }
    }
}

@PreviewMainScreen
@Composable
private fun ProfileScreenPreview() {
    PileyTheme {
        Surface {
            ProfileScreen(
                viewState = ProfileViewState(
                    userName = "John Doe",
                    doneTasks = 244,
                    currentTasks = 13,
                    deletedTasks = 3,
                    upcomingTaskList = previewUpcomingTasksList,
                    tasksCompletedPastDays = 27,
                    completedTaskFrequencies = listOf(1, 7, 8, 2, 5, 3, 1),
                    biggestPileName = "Daily",
                )
            )
        }
    }
}

@PreviewMainScreen
@Composable
private fun TaskDetailScreenPreview() {
    PileyTheme {
        Surface {
            TaskDetailScreen(
                viewState = TaskDetailViewState(
                    task = previewUpcomingTasksList[0].second,
                    titleTextValue = "Clean room",
                    piles = listOf(Pair(0, "Daily"), Pair(1, "Shopping List")),
                    reminderDateTimeText = "2023-08-04 09:36",
                )
            )
        }
    }
}

@PreviewMainScreen
@Composable
private fun PileDetailScreenPreview() {
    PileyTheme {
        Surface {
            PileDetailScreen(
                viewState = PileDetailViewState(
                    pile = previewPileWithTasksList[0].pile,
                    titleTextValue = "Daily",
                    // very long description
                    descriptionTextValue = "A list of daily tasks including cleaning, organizing, and other chores to maintain a structured schedule.",
                    completedTaskCounts = listOf(2, 6, 7, 3, 1, 1, 3),
                    doneCount = 3,
                    currentCount = 4,
                    deletedCount = 1,
                )
            )
        }
    }
}