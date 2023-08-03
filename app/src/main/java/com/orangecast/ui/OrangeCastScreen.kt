package com.orangecast.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.orangepie.base.ui.theme.OrangeCastTheme
import dev.orangepie.navigation.NavigationComponent

@Composable
fun OrangeCastScreen() {
    val scaffoldState = rememberScaffoldState()
    OrangeCastTheme {
        Scaffold(
            scaffoldState = scaffoldState,
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding(),
                    )
            ) {
                NavigationComponent(paddingValues = innerPadding)
            }
        }
    }
}