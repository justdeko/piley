package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun EditUserContent(
    modifier: Modifier = Modifier,
    existingEmail: String,
    existingName: String,
    onConfirm: (EditUserResult) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var email by rememberSaveable { mutableStateOf(existingEmail) }
    var name by rememberSaveable { mutableStateOf(existingName) }
    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    Card {
        Column(
            modifier.padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Modify your personal data here.",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Name") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = { Text("New Password (leave empty if unchanged)") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                value = oldPassword,
                onValueChange = { oldPassword = it },
                placeholder = { Text("Current password to confirm") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ElevatedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("Cancel")
                }
                ElevatedButton(
                    onClick = {
                        onConfirm(
                            EditUserResult(
                                email = email,
                                name = name,
                                oldPassword = oldPassword,
                                newPassword = newPassword
                            )
                        )
                    },
                    enabled = oldPassword.isNotBlank() && email.isNotBlank() && name.isNotBlank()
                ) {
                    Text("Update")
                }
            }
        }
    }
}

data class EditUserResult(
    val email: String,
    val name: String,
    val oldPassword: String,
    val newPassword: String
)

@Preview
@Composable
fun EditUserContentPreview() {
    PileyTheme(useDarkTheme = true) {
        EditUserContent(
            modifier = Modifier.fillMaxWidth(),
            existingEmail = "thomas@test.com",
            existingName = "Thomas"
        )
    }
}