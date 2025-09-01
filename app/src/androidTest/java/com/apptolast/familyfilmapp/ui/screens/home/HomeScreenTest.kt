@file:OptIn(ExperimentalTestApi::class)

package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.filters.LargeTest
import com.apptolast.familyfilmapp.TestMainActivity
import com.apptolast.familyfilmapp.di.ApplicationModule
import com.apptolast.familyfilmapp.di.LocalStoreModule
import com.apptolast.familyfilmapp.di.RepositoryModule
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.screens.detail.MovieDetailScreen
import com.apptolast.familyfilmapp.ui.screens.login.LoginScreen
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_HOME_SEARCH_TEXT_FIELD
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
@HiltAndroidTest
@UninstallModules(
    ApplicationModule::class,
//    FirebaseModule::class,
//    GoogleSignInModule::class,
    LocalStoreModule::class,
//    NetworkModule::class,
    RepositoryModule::class,
)
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule =
        createAndroidComposeRule<TestMainActivity>()

//    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.setContent {
            FamilyFilmAppTheme {
//                navController = TestNavHostController(LocalContext.current)
                val navController = rememberNavController()
                navController.navigatorProvider.addNavigator(ComposeNavigator())

                NavHost(
                    navController = navController,
                    startDestination = Routes.Home.routes,
                ) {
                    composable(route = Routes.Login.routes) {
                        LoginScreen(navController)
                    }

                    composable(route = Routes.Home.routes) {
                        HomeScreen(
                            onClickNav = { route ->
                                navController.navigate(route)
                            },
                        )
                    }

                    composable(route = Routes.Details.routes) {
                        MovieDetailScreen(
                            movie = Movie().copy(
                                title = "Matrix",
                                overview = """
                        "Trata sobre un programador que descubre que la realidad en la que vive es
                         una simulación creada por máquinas."
                                """.trimIndent(),
                                posterPath = "https://image.tmdb.org/t/p/w500/ar2h87jlTfMlrDZefR3VFz1SfgH.jpg",
                            ),
                            onBack = {
                                navController.popBackStack()
                            },
                        )
                    }
                }
            }
        }
    }

    @Test
    fun homeScreenTest_searchTextField_isSelected() {
        composeTestRule
            .onNodeWithTag(testTag = TT_HOME_SEARCH_TEXT_FIELD)
            .assertIsNotFocused()

        composeTestRule.onNodeWithTag(TT_HOME_SEARCH_TEXT_FIELD).performClick()

        composeTestRule
            .onNodeWithTag(testTag = TT_HOME_SEARCH_TEXT_FIELD)
            .assertIsFocused()
    }

//    @Test
//    fun homeScreenTest_navigateToDetailsMovieScreen_onClick() {
//        val testTag = TT_HOME_MOVIE_ITEM + "0"
//
//        // Perform a click on the movie item
//        composeTestRule.onNodeWithTag(testTag).isDisplayed()
//        composeTestRule.onNodeWithTag(testTag).performClick()
//
//        // Assert that the navigation occurred to the details route
// //        composeTestRule
// //            .onNodeWithContentDescription(testTag)
// //            .assertIsDisplayed()
//
// //        Assert.assertEquals(navController.currentDestination?.route, Routes.Details.routes)
//
//        // Or, if you have a specific UI element on the details screen:
//        // composeTestRule.onNodeWithTag("details_screen_title").assertIsDisplayed()
//    }
}
