package com.sorsix.petadoption.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "tokens")
data class PasswordResetToken(
        @Id
        val token: String,

        @OneToOne
        val user: User,

        @JsonIgnore
        val timestamp: LocalDateTime
)