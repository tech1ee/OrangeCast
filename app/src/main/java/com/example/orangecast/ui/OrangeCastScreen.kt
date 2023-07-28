package com.example.orangecast.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.OrangeCastTheme
import dev.orangepie.navigation.NavigationComponent

@Composable
fun OrangeCastScreen() {
    val systemUiController = rememberSystemUiController()
    val scaffoldState = rememberScaffoldState()

    OrangeCastTheme {
        SideEffect {
            systemUiController.setSystemBarsColor(Color.Black, darkIcons = false)
            systemUiController.setNavigationBarColor(Color.Black, darkIcons = false)
        }
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = { scaffoldState.snackbarHostState },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(
                        bottom = innerPadding.calculateBottomPadding(),
                        top = innerPadding.calculateTopPadding(),
                    )
            ) {
                NavigationComponent(paddingValues = innerPadding)
            }
        }
    }
}