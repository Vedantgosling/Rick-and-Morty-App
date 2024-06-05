package com.example.network

import com.example.network.models.domain.Character
import com.example.network.models.domain.CharacterPage
import com.example.network.models.domain.Episode
import com.example.network.models.remote.RemoteCharacter
import com.example.network.models.remote.RemoteCharacterPage
import com.example.network.models.remote.RemoteEpisode
import com.example.network.models.remote.toDomainCharacter
import com.example.network.models.remote.toDomainCharacterPage
import com.example.network.models.remote.toDomainEpisode
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorClient {
    private val client = HttpClient(OkHttp) {
        defaultRequest { url("https://rickandmortyapi.com/api/") }


        install(Logging) {
            logger = Logger.SIMPLE
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    private var characterCache = mutableMapOf<Int, Character>()

    suspend fun getCharacter(id:Int): APIOperation<Character> {
        characterCache[id]?.let { return APIOperation.Success(it) }// check if we have cached data,
        return safeAPIcall {//this will not run if we found the mapped character
            client.get("character/$id")
                .body<RemoteCharacter>()
                .toDomainCharacter()
                .also { characterCache[id] = it } // it = todomaincharacter data generated passed to "it"
        }

    }

    suspend fun getCharacterByPage(pageNumber: Int): APIOperation<CharacterPage> {
        return safeAPIcall {
            client.get("character/?page=$pageNumber")
                .body<RemoteCharacterPage>()
                .toDomainCharacterPage()
        }
    }



    suspend fun getEpisode(episodesID: Int): APIOperation<Episode>{
        return safeAPIcall {
            client.get("episode/$episodesID")
                .body<RemoteEpisode>()
                .toDomainEpisode()
        }
    }


    suspend fun getEpisodes(episodesIDs:List<Int>):APIOperation<List<Episode>>{
        return if (episodesIDs.size == 1){
            getEpisode(episodesIDs[0]).mapSuccess {
                listOf(it)
            }
        }else{
            val idsCommaSeprated = episodesIDs.joinToString(separator = ",")
            safeAPIcall {
                client.get("episode/$idsCommaSeprated")
                    .body<List<RemoteEpisode>>()
                    .map { it.toDomainEpisode() }
            }
        }
    }




    private inline fun<T> safeAPIcall(apicall:()->T):APIOperation<T>{
        return try {
            APIOperation.Success(apicall())
        }catch (e:Exception){
            APIOperation.failure(exception = e)
        }

    }

}







sealed interface APIOperation<T>{
    data class Success<T>(val data: T):APIOperation<T>
    data class failure<T>(val exception:Exception):APIOperation<T>

    fun <R> mapSuccess(transform:(T)->R):APIOperation<R>{
        return when(this){
            is Success->Success(transform(data))
            is failure->failure(exception)
        }
    }

    fun onSuccess(block:(T)-> Unit):APIOperation<T>{
        if(this is Success) block(data)
        return this
    }

    fun onFailure(block: (Exception)->Unit):APIOperation<T>{
        if (this is failure) block(exception)
        return this
    }
}

