package com.keremsen.wordmaster.model

data class User(
   val uid: String = "",
   val email: String = "",
   val name: String = "",
   val level: Int = 1,
   val createdAt: String = "",
   val isAdmin: Boolean = false
)


