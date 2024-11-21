//package com.example.psychika
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.lifecycle.Observer
//import com.example.psychika.data.local.room.ChatMessageDao
//import com.example.psychika.data.network.Result
//import com.example.psychika.data.network.response.Geometry
//import com.example.psychika.data.network.response.Location
//import com.example.psychika.data.network.response.MessageErrorResponse
//import com.example.psychika.data.network.response.NearbyPlacesResponse
//import com.example.psychika.data.network.response.Northeast
//import com.example.psychika.data.network.response.OpeningHours
//import com.example.psychika.data.network.response.PlusCode
//import com.example.psychika.data.network.response.ResultsItem
//import com.example.psychika.data.network.response.Southwest
//import com.example.psychika.data.network.response.Viewport
//import com.example.psychika.data.network.retrofit.ClassificationApiService
//import com.example.psychika.data.network.retrofit.NearbyPlacesService
//import com.example.psychika.data.network.retrofit.PsychikaApiService
//import com.example.psychika.data.repository.PsychikaRepository
//import com.google.firebase.auth.FirebaseAuth
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.TestCoroutineDispatcher
//import kotlinx.coroutines.test.runBlockingTest
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
////class PsychikaRepositoryTest {
////
////    @get:Rule
////    val instantTaskExecutorRule = InstantTaskExecutorRule()
////
////    private lateinit var psychikaRepository: PsychikaRepository
////    private lateinit var mapsNearbyPlacesService: NearbyPlacesService
////    private val testDispatcher = TestCoroutineDispatcher()
////
////    private lateinit var observer: Observer<Result<NearbyPlacesResponse, MessageErrorResponse>>
////
////    @Before
////    fun setUp() {
////        // Mock instances of OpeningHours
////        val mockOpeningHours = OpeningHours(openNow = true)
////
////        // Mock instances for Southwest and Northeast
////        val mockSouthwest = Southwest(
////            lng = 56.00, // Assuming you can use Double here
////            lat = 12.00
////        )
////
////        val mockNortheast = Northeast(
////            lng = 57.00,
////            lat = 13.00
////        )
////
////        // Mock Viewport instance
////        val mockViewport = Viewport(
////            southwest = mockSouthwest,
////            northeast = mockNortheast
////        )
////
////        // Mock Location instance
////        val mockLocation = Location(lat = 12.34, lng = 56.78) // Correctly defined Location instance
////
////        // Mock Geometry instance
////        val mockGeometry = Geometry(location = mockLocation, viewport = mockViewport)
////
////        // Mock PlusCode instance
////        val mockPlusCode = PlusCode(
////            compoundCode = "XYZ123",
////            globalCode = "XYZ1234"
////        )
////
////        // Mocked instances of ResultsItem
////        val mockResults = listOf(
////            ResultsItem(
////                types = listOf("restaurant", "food"),
////                businessStatus = "OPERATIONAL",
////                icon = "https://example.com/icon.png",
////                rating = 4.5,
////                iconBackgroundColor = "#FFFFFF",
////                photos = listOf(/* Add mocked PhotosItem instances if necessary */),
////                reference = "some_reference",
////                userRatingsTotal = 100,
////                scope = "GOOGLE",
////                name = "Mocked Place 1",
////                openingHours = mockOpeningHours,
////                geometry = mockGeometry,
////                iconMaskBaseUri = "https://example.com/icon_mask",
////                vicinity = "123 Mock St, Mock City",
////                plusCode = mockPlusCode,
////                placeId = "mock_place_id",
////                permanentlyClosed = false
////            ),
////            ResultsItem(
////                types = listOf("cafe"),
////                businessStatus = "OPERATIONAL",
////                icon = "https://example.com/icon2.png",
////                rating = 4.0,
////                iconBackgroundColor = "#FFFFFF",
////                photos = listOf(/* Add mocked PhotosItem instances if necessary */),
////                reference = "some_other_reference",
////                userRatingsTotal = 50,
////                scope = "GOOGLE",
////                name = "Mocked Place 2",
////                openingHours = mockOpeningHours,
////                geometry = mockGeometry,
////                iconMaskBaseUri = "https://example.com/icon_mask2",
////                vicinity = "456 Mock Ave, Mock City",
////                plusCode = mockPlusCode,
////                placeId = "mock_place_id_2",
////                permanentlyClosed = false
////            )
////        )
////
////        mapsNearbyPlacesService = object : NearbyPlacesService {
////            override suspend fun getNearbyPlaces(
////                type: String,
////                location: String,
////                radius: Int,
////                apiKey: String
////            ): NearbyPlacesResponse {
////                return NearbyPlacesResponse(
////                    nextPageToken = "mocked_next_page_token",
////                    htmlAttributions = listOf("Mocked attribution"),
////                    results = mockResults,
////                    status = "OK"
////                )
////            }
////        }
////
////        psychikaRepository = PsychikaRepository(
////            psychikaApiService = object : PsychikaApiService { /* Mocked implementation */ },
////            classificationApiService = object : ClassificationApiService { /* Mocked implementation */ },
////            mapsNearbyPlacesService = mapsNearbyPlacesService,
////            firebaseAuth = object : FirebaseAuth { /* Mocked implementation */ },
////            chatMessageDao = object : ChatMessageDao { /* Mocked implementation */ }
////        )
////
////        observer = Observer {}
////    }
////}
