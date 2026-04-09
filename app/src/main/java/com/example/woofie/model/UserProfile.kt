package com.example.woofie.model

data class UserProfile(
	val profession: Profession,
	val level: String,
	var streak: Int,
	var completedLessons: Int,
	var xp: Int
)

