package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// AquaConnect Premium Retro-Utility Palette
val AquaCanvasBg = Color(0xFFF8F6F0)        // Warm cream/eggshell background
val AquaCharcoal = Color(0xFF1A2522)        // Dark slate/charcoal main text
val AquaMutedCharcoal = Color(0xFF5C6663)   // Secondary text/muted labels
val AquaBorderMuted = Color(0xFFDCD9D0)     // Light warm grey border line
val AquaSurfaceWhite = Color(0xFFFFFFFF)    // Pure white container surface
val AquaHoverBg = Color(0xFFF2EFE8)         // Soft warm grey hover/select highlight

// Accents
val AquaOchre = Color(0xFFB9721D)           // Warm ochre/bronze/gold accent
val AquaOchreContainer = Color(0xFFF5EAD7)  // Soft light ochre/gold
val AquaTeal = Color(0xFF2C7B70)            // Cool teal/sage accent
val AquaTealContainer = Color(0xFFE2F0EE)   // Soft light teal/sage

// Dark Mode Retro-Utility Palette
val AquaDarkCanvasBg = Color(0xFF141D1C)    // Dark technical background
val AquaDarkCharcoal = Color(0xFFF2EFE8)    // Light cream main text
val AquaDarkMutedCharcoal = Color(0xFFA5B0AD) // Secondary text/muted labels
val AquaDarkBorderMuted = Color(0xFF2F3C3A) // Darker border line
val AquaDarkSurface = Color(0xFF1E2927)     // Slate surface container
val AquaDarkHoverBg = Color(0xFF253230)     // Darker hover highlight

// Dark Mode Accents
val AquaDarkOchre = Color(0xFFD4872C)
val AquaDarkOchreContainer = Color(0xFF3B2F1F)
val AquaDarkTeal = Color(0xFF3E9B8F)
val AquaDarkTealContainer = Color(0xFF1A3330)

// Pipeline Status Colors (Minimalist Retro Utility style)
val StatusPending = Color(0xFFB9721D)         // Ochre/Amber
val StatusProcessing = Color(0xFF2C7B70)      // Cool Teal
val StatusPacked = Color(0xFF4A6F82)          // Slate Blue
val StatusPickedUp = Color(0xFF7C5A9F)        // Muted Purple
val StatusOutForDelivery = Color(0xFF3E8D7F)  // Sage Green
val StatusDelivered = Color(0xFF2E7D32)       // Forest Green
val StatusCancelled = Color(0xFFC62828)       // Dark Crimson Red
// Legacy color token aliases mapping to new retro-utility tokens (used in AuthScreens.kt)
val AquaPrimary = AquaTeal
val AquaSecondary = AquaOchre
val AquaOnBackground = AquaCharcoal
val AquaBackground = AquaCanvasBg
val GlassBorderWhite = Color(0xFFFFFFFF).copy(alpha = 0.3f)
val GlassSurfaceWhite = Color(0xFFFFFFFF).copy(alpha = 0.15f)
