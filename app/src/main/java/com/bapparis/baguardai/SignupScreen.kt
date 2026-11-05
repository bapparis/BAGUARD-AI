package com.bapparis.baguardai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SignupBackground = Color(0xFF0B0720)
private val SignupViolet = Color(0xFF8B5CF6)
private val SignupPurple = Color(0xFF6D28D9)
private val SignupWhite = Color(0xFFF5F3FF)
private val SignupMuted = Color(0xFFAAA4C5)

@Composable
fun SignupScreen(
    onBack: () -> Unit,
    onLogin: () -> Unit,
    onPrivacyPolicy: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    var showUnavailableMessage by remember { mutableStateOf(false) }

    val validEmail = android.util.Patterns.EMAIL_ADDRESS
        .matcher(email.trim()).matches()

    val passwordsMatch =
        password.isNotEmpty() && password == confirmPassword

    val formValid =
        fullName.isNotBlank() &&
                username.isNotBlank() &&
                validEmail &&
                password.length >= 8 &&
                passwordsMatch

    val canContinue = formValid && termsAccepted

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SignupBackground)
    ) {

        // Background glow effects
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = 190.dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0x557C3AED),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-100).dp, y = 400.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0x445B21B6),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 30.dp)
        ) {

            // Back button
            Text(
                text = "←",
                color = SignupWhite,
                fontSize = 28.sp,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Security emblem
            Box(
                modifier = Modifier
                    .size(76.dp)
            .align(Alignment.CenterHorizontally)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF7C3AED),
                        Color(0xFF2563EB)
                    )
                ),
                CircleShape
            ),
            contentAlignment = Alignment.Center
            ) {
            Text(
                text = "🛡",
                fontSize = 36.sp
            )
        }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "SIGN UP",
                color = SignupWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Join BAGUARD AI and stay protected",
                color = SignupMuted,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(30.dp))

            SignupInput(
                label = "FULL NAME",
                value = fullName,
                onValueChange = {
                    fullName = it
                    showUnavailableMessage = false
                },
                placeholder = "Enter your full name"
            )

            SignupInput(
                label = "USERNAME",
                value = username,
                onValueChange = {
                    username = it
                    showUnavailableMessage = false
                },
                placeholder = "Choose a username"
            )

            SignupInput(
                label = "EMAIL ADDRESS",
                value = email,
                onValueChange = {
                    email = it
                    showUnavailableMessage = false
                },
                placeholder = "Enter your email",
                isEmail = true
            )

            SignupInput(
                label = "PASSWORD",
                value = password,
                onValueChange = {
                    password = it
                    showUnavailableMessage = false
                },
                placeholder = "Minimum 8 characters",
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = {
                    passwordVisible = !passwordVisible
                }
            )

            SignupInput(
                label = "CONFIRM PASSWORD",
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    showUnavailableMessage = false
                },
                placeholder = "Confirm your password",
                isPassword = true,
                passwordVisible = confirmVisible,
                onTogglePassword = {
                    confirmVisible = !confirmVisible
                }
            )

            if (password.isNotEmpty() && password.length < 8) {
                Text(
                    text = "Password must contain at least 8 characters.",
                    color = Color(0xFFF87171),
                    fontSize = 12.sp
                )
            }

            if (confirmPassword.isNotEmpty() && !passwordsMatch) {
                Text(
                    text = "Passwords do not match.",
                    color = Color(0xFFF87171),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Terms agreement
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = {
                        termsAccepted = it
                        showUnavailableMessage = false
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = SignupViolet,
                        uncheckedColor = SignupMuted,
                        checkmarkColor = Color.White
                    )
                )

                Column {
                    Text(
                        text = "I agree to the Terms of Service",
                        color = SignupWhite,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "and Privacy Policy",
                        color = SignupViolet,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable {
                            onPrivacyPolicy()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Create account button
            Button(
                onClick = {
                    showUnavailableMessage = true
                },
                enabled = canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SignupPurple,
                    disabledContainerColor = Color(0xFF29213F),
                    contentColor = Color.White,
                    disabledContentColor = Color(0xFF77718A)
                )
            ) {
                Text(
                    text = "CREATE ACCOUNT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (showUnavailableMessage) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Account registration is not available yet. Please check back soon.",
                    color = Color(0xFFFBBF24),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = SignupMuted,
                    fontSize = 13.sp
                )

                Text(
                    text = "Login",
                    color = SignupViolet,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable {
                        onLogin()
                    }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Powered by BAGLOT  ✦  META",
                color = SignupMuted,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun SignupInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    isEmail: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 17.dp)
    ) {

        Text(
            text = label,
            color = SignupMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    placeholder,
                    color = SignupMuted.copy(alpha = 0.65f),
                    fontSize = 13.sp
                )
            },
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = {
                if (isPassword) {
                    Text(
                        text = if (passwordVisible) "HIDE" else "SHOW",
                        color = SignupViolet,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onTogglePassword() }
                            .padding(8.dp)
                    )
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SignupViolet,
                unfocusedBorderColor = Color(0xFF39304F),
                focusedTextColor = SignupWhite,
                unfocusedTextColor = SignupWhite,
                cursorColor = SignupViolet,
                focusedContainerColor = Color(0xFF171127),
                unfocusedContainerColor = Color(0xFF121020)
            )
        )
    }
}
