package com.example.madproject.data.remote.auth

import com.example.madproject.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val username: String,
    val email: String
)

@Serializable
data class ProfileInsert(
    val id: String,
    val username: String,
    val email: String
)

class AuthRepository {

    private val supabase = SupabaseProvider.client

    suspend fun signUp(
        username: String,
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val userId = supabase.auth.currentUserOrNull()?.id
                ?: error("Signup succeeded but no user session/user id was available.")

            supabase.postgrest["profiles"].insert(
                ProfileInsert(
                    id = userId,
                    username = username,
                    email = email
                )
            )
        }
    }

    suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    suspend fun signOut(): Result<Unit> {
        return runCatching {
            supabase.auth.signOut()
        }
    }

    suspend fun getProfile(): Result<Profile> {
        return runCatching {
            val userId = currentUserId() ?: error("No signed-in user.")

            supabase.postgrest["profiles"]
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Profile>()
        }
    }

    suspend fun updateUsername(newUsername: String): Result<Profile> {
        return runCatching {
            val userId = currentUserId() ?: error("No signed-in user.")

            supabase.postgrest["profiles"]
                .update(
                    {
                        set("username", newUsername)
                    }
                ) {
                    select()
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Profile>()
        }
    }

    fun currentUserId(): String? = supabase.auth.currentUserOrNull()?.id

    fun currentUserEmail(): String? = supabase.auth.currentUserOrNull()?.email

    fun isLoggedIn(): Boolean = supabase.auth.currentUserOrNull() != null
}