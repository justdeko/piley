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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.common.TextWithCheckbox
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.IndefiniteProgressBar
import com.dk.piley.util.isValidEmail
import com.dk.piley.util.navigateClearBackstack

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val viewState by viewModel.state.collectAsState()
    val context = LocalContext.current

    if (viewState.signInState == SignInState.SIGNED_IN) {
        LaunchedEffect(viewState.signInState) {
            navController.navigateClearBackstack(Screen.Pile.route)
        }
    }
    if (viewState.toastMessage != null) {
        LaunchedEffect(key1 = viewState.toastMessage) {
            Toast.makeText(context, viewState.toastMessage, Toast.LENGTH_SHORT).show()
            viewModel.setToastMessage(null)
        }
    }

    SignInScreen(
        modifier = modifier,
        viewState = viewState,
        onEmailChange = { viewModel.setEmail(it) },
        onUsernameChange = { viewModel.setUsername(it) },
        onPasswordChange = { viewModel.setPassword(it) },
        onAttemptSignIn = { viewModel.attemptSignIn() },
        onChangeRegister = {
            if (viewState.signInState == SignInState.REGISTER) {
                viewModel.setSignInState(SignInState.SIGNED_OUT)
            } else {
                viewModel.setSignInState(SignInState.REGISTER)
            }
        },
        onChangeOfflineRegister = { isOffline ->
            if (isOffline) {
                viewModel.setSignInState(SignInState.REGISTER_OFFLINE)
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
    onChangeRegister: () -> Unit = {},
    onChangeOfflineRegister: (Boolean) -> Unit = {}
) {
    val isRegister =
        viewState.signInState == SignInState.REGISTER || viewState.signInState == SignInState.REGISTER_OFFLINE
    val signInText = if (isRegister) "Register" else "Sign In"
    var emailFocused by remember { mutableStateOf(false) }
    val emailError =
        !viewState.email.isValidEmail() && viewState.email.isNotBlank() && !emailFocused

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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .onFocusChanged { emailFocused = it.isFocused },
                value = viewState.email,
                onValueChange = onEmailChange,
                placeholder = { Text("Email") },
                shape = RoundedCornerShape(16.dp),
                isError = emailError,
                supportingText = if (emailError) {
                    @Composable
                    {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Not a valid email",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
            )
            AnimatedVisibility(
                viewState.signInState == SignInState.REGISTER
                        || viewState.signInState == SignInState.REGISTER_OFFLINE
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                value = viewState.password,
                onValueChange = onPasswordChange,
                placeholder = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
            )
            AnimatedVisibility(
                viewState.signInState == SignInState.REGISTER
                        || viewState.signInState == SignInState.REGISTER_OFFLINE
            ) {
                TextWithCheckbox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp),
                    description = "Make user offline",
                    checked = viewState.signInState == SignInState.REGISTER_OFFLINE,
                    onChecked = onChangeOfflineRegister
                )
            }
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
    val signInEnabled =
        viewState.email.isNotBlank() && viewState.password.isNotBlank() && viewState.email.isValidEmail()
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
                signInState = SignInState.REGISTER
            )
            SignInScreen(viewState = state)
        }
    }
}