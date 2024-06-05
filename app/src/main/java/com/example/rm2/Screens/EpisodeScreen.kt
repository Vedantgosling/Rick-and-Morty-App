package com.example.rm2.Screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.KtorClient
import com.example.network.models.domain.Character
import com.example.network.models.domain.Episode
import com.example.rm2.Common.CharacImage
import com.example.rm2.Common.CharacterNameScreen
import com.example.rm2.components.Episode.EpisodeRowComponent
import com.example.rm2.ui.theme.RickPrimary
import com.example.rm2.ui.theme.RickTextPrimary
import kotlinx.coroutines.launch


@Composable
fun EpisodeScreen(
    characterId:Int,
    ktorClient: KtorClient
) {
    var characterState by remember { mutableStateOf<Character?>(null) } //Null is the default
    var episodesState by remember { mutableStateOf<List<Episode>>(emptyList()) }

    LaunchedEffect(key1 = Unit, block = {
        ktorClient.getCharacter(characterId).onSuccess {character->
            characterState = character
            launch {
                ktorClient.getEpisodes(character.episodeIds).onSuccess { episodes ->
                    episodesState = episodes
                }.onFailure {
                    // TODO: Later 
                }
            }
        }.onFailure {
            // TODO: Later 
        }
    })

    characterState?.let {character->
        MainScreen(character = character, episodes = episodesState)
    }?: CircularProgressIndicator()

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainScreen(character: Character, episodes: List<Episode>){
    LazyColumn(contentPadding = PaddingValues(all = 16.dp)) {
        item { CharacterNameScreen(name = character.name) }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { CharacImage(imageUrl = character.imageUrl) }



        episodes.groupBy { it.seasonNumber }.forEach{mapEntry->
           stickyHeader{ SeasonHeader(seasonNumber = mapEntry.key)}
            item { Spacer(modifier = Modifier.height(15.dp)) }
            items(mapEntry.value){episode->
                EpisodeRowComponent(episode = episode)
                Spacer(modifier = Modifier.height(15.dp))

            }

        }
    }

}



@Composable
private fun SeasonHeader(seasonNumber: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = RickPrimary)
            .padding(top = 8.dp, bottom = 16.dp)
            .border(
                width = 1.dp,
                color = RickTextPrimary,
                shape = RoundedCornerShape(16.dp)
            )

    ) {
        Text(
            text = "Season $seasonNumber",
            color = RickTextPrimary,
            fontSize = 32.sp,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}