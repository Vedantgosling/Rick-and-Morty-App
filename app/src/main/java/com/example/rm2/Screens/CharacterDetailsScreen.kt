package com.example.rm2.Screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.rm2.ViewModels.CharacterDetailsViewModel
import com.example.rm2.ViewModels.CharacterDetailsViewState
import com.example.rm2.components.CharacterDetailNamePlateComponent
import com.example.rm2.components.DataPointComponent
import com.example.rm2.ui.theme.RickAction


@Composable
fun CharacterDetailsScreen(
    characterId:Int,
    viewModel: CharacterDetailsViewModel = hiltViewModel(),
    onEpisodeClicked:(Int)->Unit
) {


    LaunchedEffect(key1 = Unit, block = {
        viewModel.fetchCharacter(characterId)
    })

    val state by viewModel.stateflow.collectAsState()


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 16.dp)
    ) {

        when (val viewstate = state) {
            CharacterDetailsViewState.Loading ->
                item{  loadingItem()           }

            is CharacterDetailsViewState.Error -> {
                //TODO
            }

            is CharacterDetailsViewState.Success -> {

                item {
                    CharacterDetailNamePlateComponent(
                        name =viewstate.character.name,
                        status = viewstate.character.status
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                item {
                    SubcomposeAsyncImage(model = viewstate.character.imageUrl,
                        contentDescription = "null",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .border(
                                width = 4.dp,
                                color = RickAction,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp)),

                        loading = { CircularProgressIndicator() }
                    )
                }

                items(viewstate.characterDataPoints) {
                    Spacer(modifier = Modifier.height(32.dp))
                    DataPointComponent(dataPoint = it)
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }

                item {

                    Text(
                        text = "View all episodes",
                        color = RickAction,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .border(
                                width = 1.dp,
                                color = RickAction,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onEpisodeClicked(characterId)
                            }

                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    )

                }

                item { Spacer(modifier = Modifier.height(64.dp)) }


            }


        }


    }
}

@Composable
fun loadingItem(){
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxSize()
            .padding(100.dp),
        color = RickAction
    )
}




