package com.example.psychika.data.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.auth0.android.jwt.JWT
import com.example.psychika.BuildConfig
import com.example.psychika.data.entity.ChatMessage
import com.example.psychika.data.entity.ChatbotRequest
import com.example.psychika.data.entity.DailyAveragePrediction
import com.example.psychika.data.entity.ErrorMessage
import com.example.psychika.data.local.room.ChatMessageDao
import com.example.psychika.data.local.room.ChatMessageEntity
import com.example.psychika.data.network.Result
import com.example.psychika.data.network.firebase.UserGoogleAuth
import com.example.psychika.data.network.response.ChatbotResponse
import com.example.psychika.data.network.response.ErrorResponse
import com.example.psychika.data.network.response.MessageErrorResponse
import com.example.psychika.data.network.response.TokenResponse
import com.example.psychika.data.network.response.ErrorsItem
import com.example.psychika.data.network.response.NearbyPlacesResponse
import com.example.psychika.data.network.response.PredictResponse
import com.example.psychika.data.network.response.StartSessionResponse
import com.example.psychika.data.network.response.SuccessResponse
import com.example.psychika.data.network.response.UnprocessableEntityResponse
import com.example.psychika.data.network.response.UserResponse
import com.example.psychika.data.network.retrofit.PsychikaApiService
import com.example.psychika.data.network.retrofit.ClassificationApiService
import com.example.psychika.data.network.retrofit.NearbyPlacesService
import com.example.psychika.ui.home.HomeFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.database
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.net.SocketTimeoutException

class PsychikaRepository(
    private val psychikaApiService: PsychikaApiService,
    private val classificationApiService: ClassificationApiService,
    private val mapsNearbyPlacesService: NearbyPlacesService,
    private val firebaseAuth: FirebaseAuth,
    private val chatMessageDao: ChatMessageDao,
    private val sharedPreferences: SharedPreferences
) {
    private val db = Firebase.database
    private val userRef = db.reference.child("users")

    fun registerApi(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): LiveData<Result<TokenResponse, ErrorsItem>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = psychikaApiService.register(firstName, lastName, email, password)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "Error Register API : $errorBody")
                val errorResponse = Gson().fromJson(errorBody, UnprocessableEntityResponse::class.java).errors
                val errorMessage = errorResponse!!.map { it?.message }
                emit(Result.Error(ErrorsItem(message = errorMessage.toString())))
            }
        }

    fun registerWithGoogleAuth(
        userId: String,
        userMap: HashMap<String, String?>
    ): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                userRef.child(userId).setValue(userMap).await()
                emit(Result.Success("Berhasil mendaftar akun ke Firebase"))
            } catch (e: Exception) {
                Log.e(HomeFragment.TAG, "Failed to register current user to firebase database")
                emit(Result.Error("Failed to register current user to firebase database"))
            }
        }

    fun loginApi(
        email: String,
        password: String
    ): LiveData<Result<TokenResponse, MessageErrorResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = psychikaApiService.login(email, password)

                val isKeyRemoved = sharedPreferences?.edit()?.remove("session_id")?.commit()

                if (isKeyRemoved == true) {
                    Log.d(TAG, "Key successfully removed from SharedPreferences.")
                } else {
                    Log.e(TAG, "Failed to remove key from SharedPreferences.")
                }

                emit(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "Error Login API : $errorBody")
                val messageErrorResponse = Gson().fromJson(errorBody, MessageErrorResponse::class.java)
                val errorMessage = messageErrorResponse.message
                emit(Result.Error(MessageErrorResponse(errorMessage)))
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Timeout Login with API : ${e.message}", )
                emit(Result.Error(MessageErrorResponse(e.message ?: "Unknown error")))
            }
        }

    fun loginWithGoogle(idToken: String): LiveData<Result<UserGoogleAuth, ErrorMessage>> =
        liveData {
            emit(Result.Loading)

            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(credential).await()
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    val isKeyRemoved = sharedPreferences?.edit()?.remove("session_id")?.commit()

                    if (isKeyRemoved == true) {
                        Log.d(TAG, "Key successfully removed from SharedPreferences.")
                    } else {
                        Log.e(TAG, "Failed to remove key from SharedPreferences.")
                    }

                    val idToken = currentUser.uid
                    val profilePic = currentUser.photoUrl.toString()
                    val name = currentUser.displayName ?: ""
                    val nameParts = name.split(" ")
                    val firstName = nameParts.getOrNull(0)
                    val lastName = nameParts.drop(1).joinToString(" ")
                    val email = currentUser.email ?: ""
                    val userGoogleAuth =
                        UserGoogleAuth(idToken, profilePic, firstName, lastName, email)
                    emit(Result.Success(userGoogleAuth))
                } else {
                    emit(Result.Error(ErrorMessage("Failed to retrieve user information")))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error Login with Google: ${e.message}", )
                emit(Result.Error(ErrorMessage(e.message ?: "Unknown error")))
            }
        }

    fun getCurrentUserApi(token: String): LiveData<Result<UserResponse, MessageErrorResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = psychikaApiService.getCurrUser(token)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "Error Get Current API : $errorBody")
                val messageErrorResponse = Gson().fromJson(errorBody, MessageErrorResponse::class.java)
                val errorMessage = messageErrorResponse.message
                emit(Result.Error(MessageErrorResponse(errorMessage)))
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Timeout Get Current User with API : ${e.message}", )
                emit(Result.Error(MessageErrorResponse(e.message ?: "Unknown error")))
            }
        }

    fun getCurrentUserGoogleAuth(): LiveData<Result<UserGoogleAuth, ErrorMessage>> =
        liveData {
            emit(Result.Loading)

            try {
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    val idToken = currentUser.uid
                    val profilePic = currentUser.photoUrl.toString()
                    val name = currentUser.displayName ?: ""
                    val nameParts = name.split(" ")
                    val firstName = nameParts.getOrNull(0)
                    val lastName = nameParts.drop(1).joinToString(" ")
                    val email = currentUser.email ?: ""
                    val userGoogleAuth = UserGoogleAuth(idToken, profilePic, firstName!!, lastName, email)
                    emit(Result.Success(userGoogleAuth))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error Get Current User Google: ${e.message}", )
                emit(Result.Error(ErrorMessage(e.message ?: "Unknown error")))
            }
        }

    fun getCurrentFirebaseUser(userId: String): LiveData<Result<UserGoogleAuth, ErrorMessage>> =
        liveData {
            emit(Result.Loading)

            try {
                val userSnapshot = userRef.child(userId).get().await()
                if (userSnapshot.exists()) {
                    val userGoogleAuth = userSnapshot.getValue(UserGoogleAuth::class.java)
                    emit(Result.Success(userGoogleAuth!!))
                } else {
                    val userGoogleAuth = UserGoogleAuth()
                    Log.d(TAG, "Not Registered : $userGoogleAuth")
                    emit(Result.Success(userGoogleAuth))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error Get Current User Google: ${e.message}", )
                emit(Result.Error(ErrorMessage(e.message ?: "Unknown error")))
            }
        }

    fun startNewSession(token: String, model: String, stream: Boolean): LiveData<Result<StartSessionResponse, ErrorResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = psychikaApiService.startSession(token, model, stream)
                Log.d(TAG, "StartSession Response: $response")
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                val errorMessage = errorResponse.message
                Log.e(TAG, "Error Start Session Repo level: $errorMessage")
                emit(Result.Error(ErrorResponse(errorMessage)))
            }
        }

    fun sendChat(token: String, message: ChatMessage, sessionId: String): LiveData<Result<ChatbotResponse, ErrorResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val request = ChatbotRequest("psychIT", message, sessionId, true)
                Log.d(TAG, "Send Chat : $request")
                val response = psychikaApiService.sendChat(token, request)
                Log.d(TAG, "Res log: $response");
                emit(Result.Success(response))
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Timeout Error Send Chat : ${e.message}", )
                emit(Result.Error(ErrorResponse(e.message ?: "Unknown error")))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                val errorMessage = errorResponse.message
                Log.e(TAG, "Error Send Chat Repo level: $errorMessage")
                emit(Result.Error(ErrorResponse(errorMessage)))
            }
        }

    fun insertMessage(chatMessage: ChatMessageEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            chatMessageDao.insertMessage(chatMessage)
        }
    }

    fun getAllMessagesByDate(date: String, userId: String): LiveData<List<ChatMessageEntity>> {
        val jwt = JWT(userId)
        val extractedId = jwt.getClaim("id").asString()

        if (extractedId.isNullOrEmpty()) {
            Log.e(TAG, "Failed to decode JWT or 'id' claim is missing")
            return MutableLiveData(emptyList())
        }

        Log.d(TAG, "Decoded userId: $extractedId")

        return chatMessageDao.getAllMessagesByDate(date, extractedId)
    }

    fun deleteChatRoleLoading() {
        CoroutineScope(Dispatchers.IO).launch {
            chatMessageDao.deleteChatRoleLoading()
        }
    }

    fun getAllDateMessages(userId: String): LiveData<List<DailyAveragePrediction>> {
        val jwt = JWT(userId)
        val extractedId = jwt.getClaim("id").asString()

        if (extractedId.isNullOrEmpty()) {
            Log.e(TAG, "Failed to decode JWT or 'id' claim is missing")
            return MutableLiveData(emptyList())
        }

        Log.d(TAG, "Decoded userId: $extractedId")

        return chatMessageDao.getAllDateMessages(extractedId)
    }

    fun getPredict(text: String): LiveData<Result<PredictResponse, ErrorResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = classificationApiService.getPredict(text)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "Error Get Predict : $errorBody")
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                val errorMessage = errorResponse.message
                emit(Result.Error(ErrorResponse(errorMessage)))
            }
        }

    fun getMapsNearbyPlaces(
        type: String,
        lat: Double,
        lng: Double,
        radius: Int
    ): LiveData<Result<NearbyPlacesResponse, MessageErrorResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = mapsNearbyPlacesService.getNearbyPlaces(type, "$lat, $lng", radius, BuildConfig.HOSPITAL_API_KEY)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "Error Get Maps Nearby Hospital : $errorBody")
                val messageErrorResponse = Gson().fromJson(errorBody, MessageErrorResponse::class.java)
                val errorMessage = messageErrorResponse.message
                emit(Result.Error(MessageErrorResponse(errorMessage)))
            }
        }

    fun updateCurrentUserAPI(
        token: String,
        firstName: String,
        lastName: String,
        email: String,
    ): LiveData<Result<SuccessResponse, MessageErrorResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = psychikaApiService.updateCurrUser(token, firstName, lastName, email)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "Error Update Current User API : $errorBody")
                val messageErrorResponse = Gson().fromJson(errorBody, MessageErrorResponse::class.java)
                val errorMessage = messageErrorResponse.message
                emit(Result.Error(MessageErrorResponse(errorMessage)))
            }
        }

    fun updateCurrentUserGoogle(
        userId: String,
        userMap: HashMap<String, String?>
    ): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                userRef.child(userId).setValue(userMap).await()
                emit(Result.Success("Berhasil mengubah data akun"))
            } catch (e: Exception) {
                Log.e(HomeFragment.TAG, "Failed to update current user to firebase database")
                emit(Result.Error("Failed to update current user to firebase database"))
            }
        }

    fun updatePasswordCurrentUserApi(
        token: String,
        currPass: String,
        newPass: String
    ): LiveData<Result<SuccessResponse, MessageErrorResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = psychikaApiService.updatePass(token, currPass, newPass)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "Error Update Password Current User API : $errorBody")
                val messageErrorResponse = Gson().fromJson(errorBody, MessageErrorResponse::class.java)
                val errorMessage = messageErrorResponse.message
                emit(Result.Error(MessageErrorResponse(errorMessage)))
            }
        }

    companion object {
        const val TAG = "PsychikaRepository"
    }
}