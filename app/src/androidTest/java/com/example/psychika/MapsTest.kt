//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry
//import com.example.psychika.data.network.response.MessageErrorResponse
//import com.example.psychika.data.network.response.NearbyPlacesResponse
//import com.example.psychika.data.repository.PsychikaRepository
//import com.google.gson.Gson
//import junit.framework.TestCase.assertEquals
//import junit.framework.TestCase.assertTrue
//import okhttp3.mockwebserver.MockResponse
//import okhttp3.mockwebserver.MockWebServer
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import kotlin.test.assertEquals
//import kotlin.test.assertTrue
//
//@RunWith(AndroidJUnit4::class)
//class PsychikaRepositoryTest {
//
//    private lateinit var mockWebServer: MockWebServer
//    private lateinit var psychikaRepository: PsychikaRepository
//    private lateinit var mapsNearbyPlacesService: MapsNearbyPlacesService
//
//    @Before
//    fun setUp() {
//        mockWebServer = MockWebServer()
//        mockWebServer.start()
//
//        // Set up your Retrofit instance with the MockWebServer URL
//        val baseUrl = mockWebServer.url("/").toString()
//        mapsNearbyPlacesService = Retrofit.Builder()
//            .baseUrl(baseUrl)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(MapsNearbyPlacesService::class.java)
//
//        psychikaRepository = PsychikaRepository(mapsNearbyPlacesService)
//    }
//
//    @After
//    fun tearDown() {
//        mockWebServer.shutdown()
//    }
//
//    @Test
//    fun testGetMapsNearbyPlaces_Success() {
//        // Prepare a mock response
//        val mockResponse = NearbyPlacesResponse(/* fill with mock data */)
//        val jsonResponse = Gson().toJson(mockResponse)
//
//        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))
//
//        // Call the function
//        val result = psychikaRepository.getMapsNearbyPlaces("hospital", 37.7749, -122.4194, 1000).getOrAwaitValue()
//
//        // Assert the result
//        assertTrue(result is Result.Success)
//        assertEquals(mockResponse, (result as Result.Success).data)
//    }
//
//    @Test
//    fun testGetMapsNearbyPlaces_Error() {
//        // Prepare a mock error response
//        val errorResponse = MessageErrorResponse("Error fetching data")
//        val jsonErrorResponse = Gson().toJson(errorResponse)
//
//        mockWebServer.enqueue(MockResponse().setResponseCode(400).setBody(jsonErrorResponse))
//
//        // Call the function
//        val result = psychikaRepository.getMapsNearbyPlaces("hospital", 37.7749, -122.4194, 1000).getOrAwaitValue()
//
//        // Assert the result
//        assertTrue(result is Result.Error)
//        assertEquals("Error fetching data", (result as Result.Error).error.message)
//    }
//}