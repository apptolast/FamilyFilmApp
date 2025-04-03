package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.filters.LargeTest
import com.apptolast.familyfilmapp.MainActivity
import com.apptolast.familyfilmapp.di.ApplicationModule
import com.apptolast.familyfilmapp.di.LocalStoreModule
import com.apptolast.familyfilmapp.di.RepositoryModule
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_HOME_SEARCH_TEXT_FIELD
import com.apptolast.familyfilmapp.utils.TT_LOGIN_EMAIL
import com.apptolast.familyfilmapp.utils.TT_LOGIN_PASS
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
        createAndroidComposeRule<MainActivity>()

//    @get:Rule(order = 1)
//    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.setContent {
            FamilyFilmAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.Home.routes,
                ) {

//                    composable(route = Routes.Login.routes) {
//                        LoginScreen(navController)
//                    }

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
        composeTestRule.onNodeWithTag(TT_LOGIN_EMAIL).performTextInput("a@a.com")
        composeTestRule.onNodeWithTag(TT_LOGIN_PASS).performTextInput("abcd123456")
//        composeTestRule.onNodeWithTag(TT_LOGIN_BUTTON).performClick()

        composeTestRule.onNodeWithTag(TT_HOME_SEARCH_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithTag("TT_HOME_SEARCH_TEXT_FIELD").assertIsSelected()

        // Add text and assert it changes
        composeTestRule.onNodeWithTag(TT_HOME_SEARCH_TEXT_FIELD).performTextInput("Film")
        composeTestRule.onNodeWithTag(TT_HOME_SEARCH_TEXT_FIELD).assertTextEquals("Film")
    }
}
