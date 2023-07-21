package com.moneytree.app.repository.network.responses

/**
 * The class representing the response body of category and diseases list
 */
data class NSJointCategoryDiseasesResponse(
    var categoryList: MutableList<NSCategoryData> = arrayListOf(),
    var diseasesList: MutableList<NSDiseasesData> = arrayListOf()
)
