package com.example.psychika.data.network.response

import com.google.gson.annotations.SerializedName

data class NearbyPlacesResponse(

	@field:SerializedName("next_page_token")
	val nextPageToken: String,

	@field:SerializedName("html_attributions")
	val htmlAttributions: List<Any>,

	@field:SerializedName("results")
	val results: List<ResultsItem>,

	@field:SerializedName("status")
	val status: String
)

data class PhotosItem(

	@field:SerializedName("photo_reference")
	val photoReference: String,

	@field:SerializedName("width")
	val width: Int,

	@field:SerializedName("html_attributions")
	val htmlAttributions: List<String>,

	@field:SerializedName("height")
	val height: Int
)

data class OpeningHours(

	@field:SerializedName("open_now")
	val openNow: Boolean
)

data class Southwest(

	@field:SerializedName("lng")
	val lng: Any,

	@field:SerializedName("lat")
	val lat: Any
)

data class Viewport(

	@field:SerializedName("southwest")
	val southwest: Southwest,

	@field:SerializedName("northeast")
	val northeast: Northeast
)

data class Northeast(

	@field:SerializedName("lng")
	val lng: Any,

	@field:SerializedName("lat")
	val lat: Any
)

data class ResultsItem(

	@field:SerializedName("types")
	val types: List<String>,

	@field:SerializedName("business_status")
	val businessStatus: String,

	@field:SerializedName("icon")
	val icon: String,

	@field:SerializedName("rating")
	val rating: Any,

	@field:SerializedName("icon_background_color")
	val iconBackgroundColor: String,

	@field:SerializedName("photos")
	val photos: List<PhotosItem>,

	@field:SerializedName("reference")
	val reference: String,

	@field:SerializedName("user_ratings_total")
	val userRatingsTotal: Int,

	@field:SerializedName("scope")
	val scope: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("opening_hours")
	val openingHours: OpeningHours,

	@field:SerializedName("geometry")
	val geometry: Geometry,

	@field:SerializedName("icon_mask_base_uri")
	val iconMaskBaseUri: String,

	@field:SerializedName("vicinity")
	val vicinity: String,

	@field:SerializedName("plus_code")
	val plusCode: PlusCode,

	@field:SerializedName("place_id")
	val placeId: String,

	@field:SerializedName("permanently_closed")
	val permanentlyClosed: Boolean
)

data class Location(

	@field:SerializedName("lng")
	val lng: Double,

	@field:SerializedName("lat")
	val lat: Double
)

data class PlusCode(

	@field:SerializedName("compound_code")
	val compoundCode: String,

	@field:SerializedName("global_code")
	val globalCode: String
)

data class Geometry(

	@field:SerializedName("viewport")
	val viewport: Viewport,

	@field:SerializedName("location")
	val location: Location
)
