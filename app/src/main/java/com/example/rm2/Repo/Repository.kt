package com.example.rm2.Repo

import com.example.network.APIOperation
import com.example.network.KtorClient
import com.example.network.models.domain.Character
import com.example.network.models.domain.CharacterPage
import javax.inject.Inject


class CharacterRepository @Inject constructor(private val ktorClient: KtorClient){

    suspend fun fetchCharacterPage(page: Int): APIOperation<CharacterPage> {
        return ktorClient.getCharacterByPage(pageNumber = page)
    }


    suspend fun fetchCharacter(characterId: Int): APIOperation<Character> {
        return ktorClient.getCharacter(characterId)
    }
}
