package com.example.myapplication.ui
import com.example.myapplication.R
import androidx.compose.foundation.LocalIndication
import androidx.compose.material3.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Language
import com.example.myapplication.viewmodel.SettingsViewModel
import com.example.myapplication.data.Theme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, navController: NavController) {

    val currentTheme by viewModel.themeState.collectAsState()
    val currentLanguage by viewModel.languageState.collectAsState()

    Scaffold(

    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

         Text(
          text = stringResource(R.string.settings_description),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
          modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth()
              )


            Spacer(Modifier.height(8.dp))

              SettingsCard(
                  title = stringResource(R.string.theme),
                  icon = Icons.Default.Palette
              ) {
                  ThemeDropdownMenu(
                      currentTheme = currentTheme,
                      onThemeSelected = { viewModel.changeTheme(it) }
                  )
              }


            Spacer(Modifier.height(10.dp))


              SettingsCard(
                  title = stringResource(R.string.language),
                  icon = Icons.Default.Language
              ) {
                  LanguageDropdownMenu(
                      currentLanguage = currentLanguage,
                      onLanguageSelected = { viewModel.changeLanguage(it) }
                  )
              }

            Spacer(Modifier.weight(1f))

            SignOutButton(onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }

            })

            Spacer(Modifier.height(32.dp))
        }

    }


}

@Composable
fun SettingsCard(
    title: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }
    val cardModifier = if (onClick != null) {
        Modifier.clickable( onClick = onClick,
            interactionSource = interactionSource,
            indication = LocalIndication.current,)
    } else {
        Modifier
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .then(cardModifier
                /*if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier*/),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                content()
            }
        }
    }
}

@Composable
fun ThemeDropdownMenu(currentTheme: Theme, onThemeSelected: (Theme) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var interactionSource = remember { MutableInteractionSource() }
    val currentContext = LocalContext.current
    val currentLayoutDirection = LocalLayoutDirection.current

    Row(
        modifier = Modifier.clickable(
            interactionSource=interactionSource,
            indication = LocalIndication.current,
            onClick={expanded=true}
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = getThemeDisplayName(currentTheme),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            Icons.Default.ArrowDropDown,
            contentDescription = "Select Theme",
            Modifier.size(24.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CompositionLocalProvider(
                LocalContext provides currentContext,
                LocalLayoutDirection provides currentLayoutDirection
            ){

                Theme.entries.forEach { theme ->
                    DropdownMenuItem(
                        text = { Text(getThemeDisplayName(theme)) },
                        onClick = {
                            onThemeSelected(theme)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun LanguageDropdownMenu(currentLanguage: Language, onLanguageSelected: (Language) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val currentContext = LocalContext.current
    val currentLayoutDirection = LocalLayoutDirection.current


    Row(
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = { expanded = true }

        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getLanguageDisplayName(currentLanguage),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            Icons.Default.ArrowDropDown,
            contentDescription = "Select Language",
            Modifier.size(24.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CompositionLocalProvider(
                LocalContext provides currentContext,
                LocalLayoutDirection provides currentLayoutDirection
            ) {
                Language.entries.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(getLanguageDisplayName(language)) },
                        onClick = {
                            onLanguageSelected(language)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun getThemeDisplayName(theme: Theme): String {
    return when (theme) {
        Theme.SYSTEM -> stringResource(id = R.string.system)
        Theme.LIGHT -> stringResource(id = R.string.light_mode)
        Theme.DARK -> stringResource(id = R.string.dark_mode)
    }
}

@Composable
fun getLanguageDisplayName(lang: Language): String {
    return when (lang) {
        Language.ENGLISH -> stringResource(R.string.lang_english)
        Language.ARABIC -> stringResource(R.string.lang_arabic)
    }
}

@Composable
fun SignOutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        /*colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E88E5)
        )*/
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Logout,
            contentDescription = "Sign Out",
            modifier = Modifier.size(20.dp),
            tint = Color.White
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.sign_out),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
    }
}