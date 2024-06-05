package com.example.rm2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.network.KtorClient
import com.example.rm2.Screens.CharacterDetailsScreen
import com.example.rm2.Screens.EpisodeScreen
import com.example.rm2.Screens.HomeScreen
import com.example.rm2.ui.theme.RickPrimary
import com.example.rm2.ui.theme.Rm2Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var ktorClient:KtorClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Rm2Theme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RickPrimary
                ) {
                    NavHost(navController = navController, startDestination = "HomeScreen") {
                        composable("HomeScreen"){
                            HomeScreen(onCharacterSelected = { characterId->
                                navController.navigate("character_details/$characterId")
                            }
                            )
                        }
                        composable(
                            route= "character_details/{characterId}",
                            arguments = listOf(navArgument("characterId") {
                            type = NavType.IntType }))
                        { backStackEntry -> val characterId:Int =
                            backStackEntry.arguments?.getInt("characterId")?:-1
                            CharacterDetailsScreen(characterId = characterId) {
                                navController.navigate("character_episodes/$it")
                            }
                        }


                        composable(
                            route="character_episodes/{characterId}",
                            arguments = listOf(
                            navArgument("characterId"){
                                type = NavType.IntType
                            })
                        )
                        { backStackEntry ->
                            val characterId:Int = backStackEntry.arguments?.getInt("characterId")?:-1
                            EpisodeScreen(characterId = characterId, ktorClient = ktorClient)




                        }
                    }


                }
            }
        }
    }

}

