package com.dk.piley.ui.task

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dk.piley.model.task.Task
import com.dk.piley.ui.theme.PileyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.junit.Rule
import org.junit.Test

class TaskDetailScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalPermissionsApi::class)
    @Test
    fun testOpenBottomDrawerAndSetReminder() {
        // Start the app
        composeTestRule.setContent {
            PileyTheme {
                val taskDetailViewState = TaskDetailViewState(Task(1,"sometask"), titleTextValue = "sometask")
                TaskDetailScreen(viewState = taskDetailViewState, permissionState = null)
            }
        }

        composeTestRule.onNodeWithContentDescription("set or delete a task reminder").performClick()
        composeTestRule.onNodeWithText("Add reminder").assertExists()

    }
}