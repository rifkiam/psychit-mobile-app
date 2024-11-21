package com.example.psychika

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.example.psychika.data.network.response.ErrorResponse
import com.example.psychika.data.network.response.PredictResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class StressLevelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var classificationApiService: FakeClassificationApiService
    private lateinit var getPredictLiveData: LiveData<Result<PredictResponse>>

    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)

        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        try {
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("LiveData value was never set.")
            }
        } finally {
            this.removeObserver(observer)
        }

        return data ?: throw IllegalStateException("LiveData value was null.")
    }

    @Before
    fun setUp() {
        classificationApiService = FakeClassificationApiService()
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun testGetPredict() = runTest {
        classificationApiService.predictResponse = PredictResponse(prediction = "Positive")

        getPredictLiveData = getPredict("Test Input")

        val observer = Observer<Result<PredictResponse>> {}

        try {
            getPredictLiveData.observeForever(observer)

            val result = getPredictLiveData.getOrAwaitValue()

            when (result) {
                is Result.Success -> {
                    assertNotNull(result.data)
                    assertEquals("Positive", result.data.prediction)
                }
                is Result.Error -> {
                    fail("Expected a successful response, but got an error: ${result.error}")
                }
                is Result.Loading -> {
                    fail("Expected a successful response, but the result is still loading")
                }
            }
        } finally {
            getPredictLiveData.removeObserver(observer)
        }
    }

    @Test
    fun testGetPredictError() = runTest {
        val errorResponse = ErrorResponse(message = "Prediction failed")
        classificationApiService.throwHttpException = HttpException(
            Response.error<ResponseBody>(
                400,
                ResponseBody.create(null, Gson().toJson(errorResponse))
            )
        )

        getPredictLiveData = getPredict("Test Input")

        val observer = Observer<Result<PredictResponse>> {}

        try {
            getPredictLiveData.observeForever(observer)

            val result = getPredictLiveData.value
            if (result is Result.Error) {
                val error = result.error
                assertNotNull(error)
                assertEquals("Prediction failed", error.message)
            } else {
                fail("Expected Result.Error, but got $result")
            }
        } finally {
            getPredictLiveData.removeObserver(observer)
        }
    }

    sealed class Result<out T> {
        object Loading : Result<Nothing>()
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val error: ErrorResponse) : Result<Nothing>()
    }

    private fun getPredict(text: String): LiveData<Result<PredictResponse>> = liveData {
        emit(Result.Loading)

        try {
            val response = classificationApiService.getPredict(text)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse))
        }
    }

    private class FakeClassificationApiService {
        var predictResponse: PredictResponse? = null
        var throwHttpException: HttpException? = null

        suspend fun getPredict(text: String): PredictResponse {
            throwHttpException?.let { throw it }
            return predictResponse!!
        }
    }
}
