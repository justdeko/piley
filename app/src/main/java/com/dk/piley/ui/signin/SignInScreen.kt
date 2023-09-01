package com.dk.piley.ui.signin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dk.piley.R
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.ui.common.CreateBaseUrlAlertDialog
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.TextWithCheckbox
import com.dk.piley.ui.nav.Screen
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.IndefiniteProgressBar
import com.dk.piley.util.isValidEmail
import com.dk.piley.util.navigateClearBackstack
import com.dk.piley.util.usernameCharacterLimit

/**
 * Sign in screen
 *
 * @param modifier generic modifier
 * @param navController generic nav controller
 * @param snackbarHostState host state to show snackbars
 * @param viewModel sign in view model
 */
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val viewState by viewModel.state.collectAsState()

    if (viewState.signInState == SignInState.SIGNED_IN) {
        // navigate to register screen if it is the user's first time
        if (viewState.firstTime) {
            LaunchedEffect(viewState.signInState) {
                navController.navigateClearBackstack(Screen.Intro.route)
            }
        } else {
            LaunchedEffect(viewState.signInState) {
                navController.navigateClearBackstack(Screen.Pile.route)
            }
        }
    }
    // snackbar handler
    viewState.message?.let { message ->
        LaunchedEffect(message, snackbarHostState) {
            snackbarHostState.showSnackbar(message)
            // reset message
            viewModel.setMessage(null)
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
        },
        onSetBaseUrlValue = { viewModel.setBaseUrl(it) }
    )
}

/**
 * Sign in screen content
 *
 * @param modifier generic modifier
 * @param viewState sign in view state
 * @param onEmailChange on input email change
 * @param onUsernameChange on input name change
 * @param onPasswordChange on input password change
 * @param onAttemptSignIn on attempt sign in (sign in/register button clicked)
 * @param onChangeRegister on set to register view
 * @param onSetBaseUrlValue on set base url value
 * @param onChangeOfflineRegister on set offline registration
 */
@Composable
private fun SignInScreen(
    modifier: Modifier = Modifier,
    viewState: SignInViewState,
    onEmailChange: (String) -> Unit = {},
    onUsernameChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onAttemptSignIn: () -> Unit = {},
    onChangeRegister: () -> Unit = {},
    onSetBaseUrlValue: (String) -> Unit = {},
    onChangeOfflineRegister: (Boolean) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val dim = LocalDim.current

    val isRegister =
        viewState.signInState == SignInState.REGISTER || viewState.signInState == SignInState.REGISTER_OFFLINE
    val signInText =
        if (isRegister) {
            stringResource(R.string.register_button_text)
        } else stringResource(R.string.sign_in_button_text)
    var emailFocused by remember { mutableStateOf(false) }
    val emailError =
        !viewState.email.isValidEmail() && viewState.email.isNotBlank() && !emailFocused

    var dialogOpen by remember { mutableStateOf(false) }
    if (dialogOpen) {
        CreateBaseUrlAlertDialog(
            initialUrlValue = viewState.baseUrlValue,
            onDismiss = { dialogOpen = false },
            onConfirm = {
                onSetBaseUrlValue(it)
                dialogOpen = false
            },
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dim.veryLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Text(
                text = signInText,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = dim.large),
                textAlign = TextAlign.Center
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dim.medium)
                    .onFocusChanged { emailFocused = it.isFocused },
                value = viewState.email,
                onValueChange = onEmailChange,
                placeholder = { Text(stringResource(R.string.user_email_placeholder)) },
                shape = MaterialTheme.shapes.large,
                isError = emailError,
                supportingText = if (emailError) {
                    @Composable
                    {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.invalid_email_hint),
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
                        .padding(top = dim.large),
                    value = viewState.username,
                    onValueChange = onUsernameChange,
                    placeholder = { Text(stringResource(R.string.username_placeholder)) },
                    shape = MaterialTheme.shapes.large,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    supportingText = {
                        if (viewState.username.isNotEmpty()) {
                            Text(
                                text = "${viewState.username.length} / $usernameCharacterLimit",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                            )
                        }
                    }
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dim.medium),
                value = viewState.password,
                onValueChange = onPasswordChange,
                placeholder = { Text(stringResource(R.string.password_placeholder)) },
                visualTransformation = PasswordVisualTransformation(),
                shape = MaterialTheme.shapes.large,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    this.defaultKeyboardAction(ImeAction.Done)
                })
            )
            AnimatedVisibility(
                viewState.signInState == SignInState.REGISTER
                        || viewState.signInState == SignInState.REGISTER_OFFLINE
            ) {
                TextWithCheckbox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dim.large, vertical = dim.medium),
                    description = stringResource(R.string.user_offline_checkbox_label),
                    checked = viewState.signInState == SignInState.REGISTER_OFFLINE,
                    onChecked = onChangeOfflineRegister
                )
            }
            ElevatedButton(
                modifier = Modifier.padding(top = dim.medium, bottom = dim.extraLarge),
                onClick = onAttemptSignIn,
                enabled = signInButtonEnabled(isRegister, viewState)
            ) {
                Text(signInText)
            }
            TextButton(onClick = onChangeRegister) {
                Text(
                    if (isRegister) {
                        stringResource(R.string.navigate_to_sign_in_button_text)
                    } else stringResource(
                        R.string.navigate_to_register_button_text
                    )
                )
            }
            AnimatedVisibility(viewState.signInState != SignInState.REGISTER_OFFLINE) {
                TextButton(onClick = { dialogOpen = true }) {
                    Text(stringResource(R.string.base_url_dialog_title))
                }
            }
        }
        IndefiniteProgressBar(visible = viewState.loading)
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