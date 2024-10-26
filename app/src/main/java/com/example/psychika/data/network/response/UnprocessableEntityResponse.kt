package com.example.psychika.data.network.response

import com.google.gson.annotations.SerializedName

data class UnprocessableEntityResponse(

	@field:SerializedName("parent")
	val parent: Parent? = null,

	@field:SerializedName("original")
	val original: Original? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("fields")
	val fields: Fields? = null,

	@field:SerializedName("errors")
	val errors: List<ErrorsItem?>? = null,

	@field:SerializedName("sql")
	val sql: String? = null
)

data class ErrorsItem(

	@field:SerializedName("validatorArgs")
	val validatorArgs: List<Any?>? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("validatorKey")
	val validatorKey: String? = null,

	@field:SerializedName("instance")
	val instance: Instance? = null,

	@field:SerializedName("origin")
	val origin: String? = null,

	@field:SerializedName("validatorName")
	val validatorName: Any? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)

data class Fields(

	@field:SerializedName("email")
	val email: String? = null
)

data class Instance(

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("id")
	val id: Any? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class Original(

	@field:SerializedName("severity")
	val severity: String? = null,

	@field:SerializedName("schema")
	val schema: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("line")
	val line: String? = null,

	@field:SerializedName("length")
	val length: Int? = null,

	@field:SerializedName("sql")
	val sql: String? = null,

	@field:SerializedName("file")
	val file: String? = null,

	@field:SerializedName("routine")
	val routine: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("constraint")
	val constraint: String? = null,

	@field:SerializedName("detail")
	val detail: String? = null,

	@field:SerializedName("parameters")
	val parameters: List<String?>? = null,

	@field:SerializedName("table")
	val table: String? = null
)

data class Parent(

	@field:SerializedName("severity")
	val severity: String? = null,

	@field:SerializedName("schema")
	val schema: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("line")
	val line: String? = null,

	@field:SerializedName("length")
	val length: Int? = null,

	@field:SerializedName("sql")
	val sql: String? = null,

	@field:SerializedName("file")
	val file: String? = null,

	@field:SerializedName("routine")
	val routine: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("constraint")
	val constraint: String? = null,

	@field:SerializedName("detail")
	val detail: String? = null,

	@field:SerializedName("parameters")
	val parameters: List<String?>? = null,

	@field:SerializedName("table")
	val table: String? = null
)
