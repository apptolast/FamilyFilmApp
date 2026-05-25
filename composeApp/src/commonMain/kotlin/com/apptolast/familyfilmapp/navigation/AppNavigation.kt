package com.apptolast.familyfilmapp.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.apptolast.familyfilmapp.analytics.TrackScreenViews
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.apptolast.familyfilmapp.ui.components.AdaptiveBanner
import com.apptolast.familyfilmapp.ui.components.BottomNavigationBar
import com.apptolast.familyfilmapp.ui.screens.chat.ChatScreen
import com.apptolast.familyfilmapp.ui.screens.detail.DetailsScreen
import com.apptolast.familyfilmapp.ui.screens.discover.DiscoverScreen
import com.apptolast.familyfilmapp.ui.screens.groups.GroupsScreen
import com.apptolast.familyfilmapp.ui.screens.home.HomeScreen
import com.apptolast.familyfilmapp.ui.screens.login.LoginScreen
import com.apptolast.familyfilmapp.ui.screens.profile.ProfileScreen
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.screen_title_chat
import familyfilmkmp.composeapp.generated.resources.screen_title_discover
import familyfilmkmp.composeapp.generated.resources.screen_title_groups
import familyfilmkmp.composeapp.generated.resources.screen_title_home
import familyfilmkmp.composeapp.generated.resources.screen_title_profile
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.authState.collectAsState()
    val analyticsTracker = koinInject<com.apptolast.familyfilmapp.analytics.AnalyticsTracker>()
    val purchaseManager = koinInject<PurchaseManager>()
    val hasRemovedAds by purchaseManager.hasRemovedAds.collectAsState()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    TrackScreenViews(navController = navController, tracker = analyticsTracker)

    val mainTabRoute = backStackEntry?.destination?.let { destination ->
        when {
            destination.hasRoute(Routes.Home::class) -> Res.string.screen_title_home
            destination.hasRoute(Routes.Discover::class) -> Res.string.screen_title_discover
            destination.hasRoute(Routes.Chat::class) -> Res.string.screen_title_chat
            destination.hasRoute(Routes.Groups::class) -> Res.string.screen_title_groups
            destination.hasRoute(Routes.Profile::class) -> Res.string.screen_title_profile
            else -> null
        }
    }
    val showChrome = authState is AuthState.Authenticated && mainTabRoute != null

    Scaffold(
        topBar = {
            if (showChrome) {
                val titleRes: StringResource = mainTabRoute
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(titleRes),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        },
        bottomBar = {
            if (showChrome) {
                Column(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                    if (!hasRemovedAds) AdaptiveBanner()
                    BottomNavigationBar(navController = navController)
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (authState is AuthState.Authenticated) Routes.Home else Routes.Login,
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
        ) {
            composable<Routes.Login> {
                LoginScreen(viewModel = authViewModel)
            }
            composable<Routes.Home> {
                HomeScreen(
                    onMediaSelected = { mediaId, mediaType ->
                        navController.navigate(Routes.Details(mediaId, mediaType.name))
                    },
                )
            }
            composable<Routes.Discover> {
                DiscoverScreen(
                    onMediaSelected = { mediaId, mediaType ->
                        navController.navigate(Routes.Details(mediaId, mediaType.name))
                    },
                )
            }
            composable<Routes.Chat> { ChatScreen() }
            composable<Routes.Groups> {
                GroupsScreen(
                    onMediaSelected = { mediaId, mediaType ->
                        navController.navigate(Routes.Details(mediaId, mediaType.name))
                    },
                )
            }
            composable<Routes.Profile> {
                ProfileScreen(authViewModel = authViewModel)
            }
            composable<Routes.Details> { entry ->
                val details: Routes.Details = entry.toRoute()
                DetailsScreen(
                    mediaId = details.mediaId,
                    mediaType = MediaType.valueOf(details.mediaType),
                    onBack = { navController.navigateUp() },
                )
            }
        }
    }
}
