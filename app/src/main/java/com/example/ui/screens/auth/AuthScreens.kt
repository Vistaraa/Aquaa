package com.example.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthRoleSelectionScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Email/Password, 1 = Phone OTP

    var emailInput by remember { mutableStateOf("alex@example.com") }
    var passwordInput by remember { mutableStateOf("password123") }
    var phoneInput by remember { mutableStateOf("+1 555-0198") }
    var otpInput by remember { mutableStateOf("1234") }

    val currentUser by authViewModel.currentUser.collectAsState()
    val otpSent by authViewModel.otpSent.collectAsState()
    val authError by authViewModel.authError.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AquaBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Ambient Light Orbs for Frosted Glass Depth
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF4DD0E1).copy(alpha = 0.50f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.20f, size.height * 0.15f),
                    radius = size.width * 0.70f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF80CBC4).copy(alpha = 0.45f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.80f, size.height * 0.70f),
                    radius = size.width * 0.80f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Banner
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(AquaPrimary, AquaSecondary)
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                        .shadow(6.dp, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "AquaConnect",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "AquaConnect",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AquaOnBackground
                )

                Text(
                    text = "Hyperlocal Water Jug Marketplace",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AquaSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Main Auth Form Card (Frosted Glass)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.5.dp, GlassBorderWhite), shape = RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = GlassSurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose Your Role",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Role Selector Toggle Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Surface(
                            onClick = { selectedRole = UserRole.CUSTOMER },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedRole == UserRole.CUSTOMER) AquaPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("role_tab_customer")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (selectedRole == UserRole.CUSTOMER) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Customer",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (selectedRole == UserRole.CUSTOMER) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            onClick = { selectedRole = UserRole.SELLER },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedRole == UserRole.SELLER) AquaSecondary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("role_tab_seller")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = if (selectedRole == UserRole.SELLER) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Seller / Dealer",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (selectedRole == UserRole.SELLER) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login Method Tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = AquaPrimary,
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Email Login", fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Phone OTP", fontWeight = FontWeight.SemiBold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (authError != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = authError!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (selectedTab == 0) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it; authViewModel.clearError() },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                authViewModel.loginWithEmail(emailInput, selectedRole == UserRole.SELLER)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedRole == UserRole.CUSTOMER) AquaPrimary else AquaSecondary
                            )
                        ) {
                            Text(
                                text = if (selectedRole == UserRole.CUSTOMER) "Sign In as Customer" else "Sign In as Seller",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        if (!otpSent) {
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it; authViewModel.clearError() },
                                label = { Text("Mobile Phone Number") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("phone_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { authViewModel.sendOtp(phoneInput) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("send_otp_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedRole == UserRole.CUSTOMER) AquaPrimary else AquaSecondary
                                )
                            ) {
                                Text("Send Verification Code", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(
                                text = "Enter 4-digit code sent to $phoneInput",
                                fontSize = 12.sp,
                                color = AquaSecondary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it; authViewModel.clearError() },
                                label = { Text("4-Digit OTP (Default: 1234)") },
                                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    authViewModel.verifyOtp(otpInput, selectedRole == UserRole.SELLER)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("verify_otp_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AquaPrimary)
                            ) {
                                Text("Verify & Continue", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Demo One-Tap Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "— QUICK DEMO SWITCH —",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { authViewModel.loginWithEmail("alex@example.com", false) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("demo_customer_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AquaPrimary)
                    ) {
                        Text("Customer Demo", fontSize = 12.sp, color = AquaPrimary, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { authViewModel.loginWithEmail("david@aquapure.com", true) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("demo_seller_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AquaSecondary)
                    ) {
                        Text("Seller Demo", fontSize = 12.sp, color = AquaSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
