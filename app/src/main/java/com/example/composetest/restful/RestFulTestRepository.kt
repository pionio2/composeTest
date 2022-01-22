package com.example.composetest.restful

import android.util.Log
import androidx.compose.runtime.Composable
import com.sample.data.PictureRequest
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.util.network.*
import kotlinx.serialization.json.Json

class RestFulTestRepository {
    companion object {
        private const val TAG = "RestFulTestRepository"
        const val BASE_URL = "http://220.85.88.73"
    }
    private val client: HttpClient

    private val json = Json {
        ignoreUnknownKeys = true // 모델에 없고, json에 있는경우 해당 key 무시
        prettyPrint = true
        isLenient = true // "" 따옴표 잘못된건 무시하고 처리
        encodeDefaults = true //null 인 값도 json에 포함 시킨다.
    }

    init {
        client = HttpClient(CIO) {
            // Header 또는 기본값
            defaultRequest {
                header("Accept", "application/json")
                header("Content-type", "application/json")
//                accept(ContentType.Application.Json)
//                contentType(ContentType.Application.Json))

//                parameter("api_key", "mykey")
            }

//            install(DefaultRequest) {
//                header("Accept", "application/json")
//                header("Content-type", "application/json")
//            }

            // Json serializer
            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
//                serializer = GsonSerializer()
//                serializer = JacksonSerializer()
            }

            // Timeout
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000L
                connectTimeoutMillis = 10_000L
                socketTimeoutMillis = 10_000L
            }

            //Logging
            install(Logging) {
                logger = object: Logger {
                    override fun log(message: String) {
                        Log.d(TAG, message)
                    }
                }
//                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }

    fun getHttpClient() = client

    fun getErrorStatus(th: Throwable): Int = when (th) {
        is RedirectResponseException -> { //Http Code: 3xx
            (th.response.status.value)
        }
        is ClientRequestException -> { //Http Code: 4xx
            (th.response.status.value)
        }
        is ServerResponseException -> { //Http Code: 5xx
            (th.response.status.value)
        }
        is UnresolvedAddressException -> { // Network Error - Internet Error
            1000
        }
        else -> 9999 // Unknown
    }

    @Throws
    suspend fun getPictureByGet(id: Int) =
        client.get<Picture>(BASE_URL + "/picture") {
            parameter("id", 0)
    }

    @Throws
    suspend fun getPictureByPost(pictureRequest: PictureRequest) =
        client.post<Picture>(BASE_URL + "/picture") {
            body = pictureRequest
    }
}