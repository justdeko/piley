package com.dk.piley.ui.signin

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
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
import com.dk.piley.util.IndefiniteProgressBar
import com.dk.piley.util.navigateClearBackstack

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val viewState by viewModel.state.collectAsState()
    if (viewState.signInState == SignInState.SIGNED_IN) {
        LaunchedEffect(viewState.signInState) {
            navController.navigateClearBackstack(Screen.Pile.route)
        }
    }
    SignInScreen(
        modifier = modifier,
        viewState = viewState,
        onEmailChange = { viewModel.setEmail(it) },
        onUsernameChange = { viewModel.setUsername(it) },
        onPasswordChange = { viewModel.setPassword(it) },
        onAttemptSignIn = { viewModel.attemptSignIn() },
        onSignInError = { viewModel.setSignInState(SignInState.SIGNED_OUT) },
        onChangeRegister = {
            if (viewState.signInState == SignInState.REGISTER) {
                viewModel.setSignInState(SignInState.SIGNED_OUT)
            } else {
                viewModel.setSignInState(SignInState.REGISTER)
            }
        }
    )
}

@Composable
private fun SignInScreen(
    modifier: Modifier = Modifier,
    viewState: SignInViewState,
    onEmailChange: (String) -> Unit = {},
    onUsernameChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onAttemptSignIn: () -> Unit = {},
    onSignInError: () -> Unit = {},
    onChangeRegister: () -> Unit = {},
) {
    val context = LocalContext.current
    val isRegister = viewState.signInState == SignInState.REGISTER
    val signInText = if (isRegister) "Register" else "Sign In"
    when (viewState.signInState) {
        SignInState.SIGNED_IN -> {
            Toast.makeText(context, "Signed in!", Toast.LENGTH_SHORT).show()
        }

        SignInState.SIGN_IN_ERROR -> {
            Toast.makeText(context, "Error signing in", Toast.LENGTH_SHORT).show()
            onSignInError()
        }

        SignInState.REGISTER_ERROR -> {
            Toast.makeText(context, "Error when attempting to register", Toast.LENGTH_LONG).show()
        }

        else -> {}
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        IndefiniteProgressBar(visible = viewState.loading)
        Column(
            modifier = Modifier.fillMaxSize(),
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
                text = signInText,
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
            AnimatedVisibility(
                viewState.signInState == SignInState.REGISTER
                    || viewState.signInState == SignInState.REGISTER_ERROR
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = viewState.username,
                    onValueChange = onUsernameChange,
                    placeholder = { Text("Username") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                )
            }
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
                enabled = signInButtonEnabled(isRegister, viewState)
            ) {
                Text(signInText)
            }
            TextButton(onClick = onChangeRegister) {
                Text(if (isRegister) "Sign In instead" else "No account? Click here to register")
            }
        }
    }
}

fun signInButtonEnabled(isRegister: Boolean, viewState: SignInViewState): Boolean {
    val signInEnabled = viewState.email.isNotBlank() && viewState.password.isNotBlank()
    if (viewState.loading) return false
    return if (isRegister) {
        signInEnabled && viewState.username.isNotBlank()
    } else signInEnabled
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

@PreviewMainScreen
@Composable
fun TaskDetailScreenPreviewLoading() {
    PileyTheme {
        Surface {
            val state = SignInViewState(
                loading = true,
                username = "John Doe",
                signInState = SignInState.REGISTER
            )
            SignInScreen(viewState = state)
        }
    }
}