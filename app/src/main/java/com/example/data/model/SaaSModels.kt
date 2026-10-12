package com.example.data.model

data class UserProfile(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val currentCompanyId: String = "company_default",
    val createdAt: Long = System.currentTimeMillis()
)

data class Company(
    val id: String = "company_default",
    val name: String = "BIZSTOCK Entreprise",
    val ownerId: String = "",
    val plan: String = "PRO",
    val createdAt: Long = System.currentTimeMillis()
)

data class Membership(
    val id: String = "",
    val userId: String = "",
    val companyId: String = "",
    val role: String = "OWNER", // OWNER, ADMIN, MANAGER, SELLER
    val active: Boolean = true,
    val joinedAt: Long = System.currentTimeMillis()
)
