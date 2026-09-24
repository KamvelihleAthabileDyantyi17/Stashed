package com.example.stashed.data.entities

// Master function for the "Sync to Cloud" button in your XML UI
suspend fun syncAllUserDataToCloud(userId: Int) {
    val userStrId = userId.toString()

    // 1. Sync User Profile
    val user = getUserById(userId)
    if (user != null) {
        firestoreDb.collection("users").document(userStrId).set(user)
    }

    // 2. Sync Categories
    val categories = getCategoriesSync(userId)
    categories.forEach { category ->
        firestoreDb.collection("users").document(userStrId)
            .collection("categories").document(category.categoryId.toString()) // <-- Fixed here
            .set(category)
    }

    Log.d("StashedSync", "Master cloud sync completed for user $userId")
}