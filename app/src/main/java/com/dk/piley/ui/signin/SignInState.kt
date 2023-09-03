package com.dk.piley.ui.signin

/**
 * Represents the state of the sign in screen,
 * based on which specific items are shown or workflows performed.
 *
 */
enum class SignInState {
    SIGNED_OUT, // user is still signed out. default state
    SIGNED_IN, // user successfully managed to sign in
    REGISTERED, // user successfully manged to register
    REGISTER, // user is in register mode, meaning he can also enter the user name and will perform register calls
    REGISTER_OFFLINE, // user is in offline register mode, similar to REGISTER but without remote calls
}