package com.altankoc.beuverse.feature.auth.domain.usecase

import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.auth.data.dto.RegisterRequestDto
import com.altankoc.beuverse.feature.auth.domain.repository.AuthRepository
import com.altankoc.beuverse.feature.profile.domain.model.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        passwordAgain: String,
        department: String
    ): Flow<Resource<Student>> {

        if (firstName.isBlank() || lastName.isBlank() || username.isBlank() ||
            email.isBlank() || password.isBlank() || department.isBlank()) {
            return flow { emit(Resource.Error("error_field_required")) }
        }

        if (!email.endsWith("@mf.karaelmas.edu.tr")) {
            return flow { emit(Resource.Error("error_invalid_email_domain")) }
        }

        if (!isEmailMatchingName(email, firstName, lastName)) {
            return flow { emit(Resource.Error("error_email_name_mismatch")) }
        }

        if (password.length < 6) {
            return flow { emit(Resource.Error("error_password_too_short")) }
        }

        if (password != passwordAgain) {
            return flow { emit(Resource.Error("error_passwords_not_match")) }
        }

        if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            return flow { emit(Resource.Error("error_username_invalid")) }
        }

        return authRepository.register(
            RegisterRequestDto(
                firstName = firstName,
                lastName = lastName,
                username = username,
                email = email,
                password = password,
                department = department
            )
        )
    }

    private fun normalizeTurkish(text: String): String {
        return text.lowercase()
            .replace('ş', 's')
            .replace('ü', 'u')
            .replace('ö', 'o')
            .replace('ç', 'c')
            .replace('ğ', 'g')
            .replace('ı', 'i')
            .replace('â', 'a')
            .replace('î', 'i')
            .replace('û', 'u')
    }

    private fun isEmailMatchingName(email: String, firstName: String, lastName: String): Boolean {
        val localPart = email.substringBefore("@")
        val parts = localPart.split(".")

        if (parts.size < 2) return false

        val normalizedFirst = normalizeTurkish(firstName)
        val normalizedLast = normalizeTurkish(lastName)

        val firstNameParts = normalizedFirst.split(" ")

        return parts.any { part ->
            firstNameParts.any { namePart -> namePart.startsWith(part) || part.startsWith(namePart) } ||
                    normalizedLast.startsWith(part) || part.startsWith(normalizedLast)
        }
    }
}