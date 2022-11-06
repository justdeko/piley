package com.dk.piley.ui.signin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val viewState by viewModel.state.collectAsState()
    SignInScreen(modifier = modifier,
        viewState = viewState,
        onEmailChange = { viewModel.setEmail(it) },
        onPasswordChange = { viewModel.setPassword(it) },
        onAttemptSignIn = { viewModel.attemptSignIn() },
        onSignIn = { navController.navigate(Screen.Pile.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignInScreen(
    modifier: Modifier = Modifier,
    viewState: SignInViewState,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onAttemptSignIn: () -> Unit = {},
    onSignIn: () -> Unit = {},
) {
    if (viewState.canSignIn) {
        onSignIn()
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.scale(1.5f),
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Sign In",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp),
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            value = viewState.email,
            onValueChange = onEmailChange,
            placeholder = { Text("Email") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            value = viewState.password,
            onValueChange = onPasswordChange,
            placeholder = { Text("Password") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )
        ElevatedButton(
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
            onClick = onAttemptSignIn,
            enabled = viewState.email.isNotBlank() && viewState.password.isNotBlank()
        ) {
            Text("Sign In")
        }
    }
}

@PreviewMainScreen
@Composable
fun TaskDetailScreenPreview() {
    PileyTheme {
        Surface {
            val state = SignInViewState()
            SignInScreen(viewState = state)
        }
    }
}