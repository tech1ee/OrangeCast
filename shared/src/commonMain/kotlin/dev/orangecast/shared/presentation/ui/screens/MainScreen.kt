@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package dev.orangecast.shared.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.orangecast.shared.presentation.ui.navigation.BottomNavItem
import dev.orangecast.shared.presentation.ui.theme.OrangeCastColors

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val navItems = listOf(
                        BottomNavItem.DISCOVER,
                        BottomNavItem.NEW_EPISODES, 
                        BottomNavItem.LIBRARY
                    )
                    
                    navItems.forEach { item ->
                        BottomNavButton(
                            item = item,
                            isSelected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo("discover") {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "discover",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            composable("discover") {
                DiscoverScreen(
                    onPodcastClick = { podcast ->
                        navController.navigate("podcast/${podcast.id}")
                    }
                )
            }
            composable("episodes") {
                NewEpisodesScreen()
            }
            composable("library") {
                LibraryScreen(
                    onPodcastClick = { podcast ->
                        navController.navigate("podcast/${podcast.id}")
                    }
                )
            }
            composable(
                "podcast/{podcastId}",
                arguments = listOf(navArgument("podcastId") { type = NavType.StringType })
            ) { backStackEntry ->
                val podcastId = backStackEntry.arguments?.getString("podcastId") ?: ""
                PodcastDetailScreen(
                    podcastId = podcastId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun BottomNavButton(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (isSelected) OrangeCastColors.primary else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.icon,
                    color = if (isSelected) Color.White else Color.Gray
                )
            }
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) OrangeCastColors.primary else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}