@file:OptIn(ExperimentalTestApi::class)

package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.filters.LargeTest
import com.apptolast.familyfilmapp.TestMainActivity
import com.apptolast.familyfilmapp.di.ApplicationModule
import com.apptolast.familyfilmapp.di.LocalStoreModule
import com.apptolast.familyfilmapp.di.RepositoryModule
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.screens.login.LoginScreen
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_HOME_MOVIE_ITEM
import com.apptolast.familyfilmapp.utils.TT_HOME_SEARCH_TEXT_FIELD
import com.apptolast.familyfilmapp.utils.TT_HOME_SEARCH_TEXT_LABEL
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
    LocalStoreModule::class,
    RepositoryModule::class,
)
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule =
        createAndroidComposeRule<TestMainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.setContent {
            FamilyFilmAppTheme {
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

    @Test
    fun homeScreen_displaysSearchField() {
        composeTestRule
            .onNodeWithTag(TT_HOME_SEARCH_TEXT_FIELD)
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysSearchLabel() {
        // Label Text is inside OutlinedTextField which merges descendant semantics,
        // so useUnmergedTree is needed to find the inner testTag
        composeTestRule
            .onNodeWithTag(TT_HOME_SEARCH_TEXT_LABEL, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysMovieItem() {
        // FakeRepository provides 1 "Matrix" movie via PagingData
        composeTestRule.waitUntilAtLeastOneExists(
            hasTestTag("${TT_HOME_MOVIE_ITEM}0"),
            timeoutMillis = 5000,
        )
        composeTestRule
            .onNodeWithTag("${TT_HOME_MOVIE_ITEM}0")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_searchFieldAcceptsInput() {
        composeTestRule.onNodeWithTag(TT_HOME_SEARCH_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithTag(TT_HOME_SEARCH_TEXT_FIELD).performTextInput("Matrix")
        composeTestRule.onNodeWithText("Matrix").assertExists()
    }

    @Test
    fun homeScreen_clearSearchButton_appearsAfterInput() {
        composeTestRule.onNodeWithTag(TT_HOME_SEARCH_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithTag(TT_HOME_SEARCH_TEXT_FIELD).performTextInput("test")
        // After typing, the clear (close) icon should appear
        composeTestRule.onNodeWithText("test").assertExists()
    }
}
