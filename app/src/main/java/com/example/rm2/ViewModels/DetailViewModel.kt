package com.example.rm2.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.models.domain.Character
import com.example.rm2.Repo.CharacterRepository
import com.example.rm2.components.DataPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
): ViewModel(){
    private val _internalStateFlow = MutableStateFlow<CharacterDetailsViewState>(
        CharacterDetailsViewState.Loading
    )

    val stateflow = _internalStateFlow.asStateFlow()

    fun fetchCharacter(characterId: Int)= viewModelScope.launch {
        _internalStateFlow.update { return@update CharacterDetailsViewState.Loading }
        characterRepository.fetchCharacter(characterId).onSuccess {character ->
            val dataPoint = buildList {

                add(DataPoint("Last known Location", character.location.name))
                add(DataPoint("Species", character.species))
                add(DataPoint("Gender", character.gender.displayName))

                character.type.takeIf { it.isNotEmpty()}?.let {type->
                    add(DataPoint("Type",type))
                }
                add(DataPoint("Origin", character.origin.name))
                add(DataPoint("Episode Count", character.episodeIds.size.toString()))


            }

            _internalStateFlow.update {
                return@update CharacterDetailsViewState.Success(
                    character = character,
                    characterDataPoints = dataPoint
                )
            }


        }.onFailure {exception->
            _internalStateFlow.update {
                return@update CharacterDetailsViewState.Error(
                    message = exception.message?: "Error"
                )
            }
        }
    }

}


sealed interface CharacterDetailsViewState{
    object Loading:CharacterDetailsViewState
    data class Error(val message:String):CharacterDetailsViewState
    data class Success(
        val character: Character,
        val characterDataPoints:List<DataPoint>
    ):CharacterDetailsViewState

}
