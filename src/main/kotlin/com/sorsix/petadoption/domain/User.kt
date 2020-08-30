package com.sorsix.petadoption.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*


@Entity
@Table(name = "users")
data class User(
        @Id
        val username: String,

        //@JsonIgnore
        val password: String,

        @ManyToOne
        val userRole: UserRole,

        val email: String,

        val active: Boolean = true

) {
    @OneToMany(
            mappedBy = "owner")
    @JsonIgnore
    val pets: Set<Pet> = HashSet()

    @ManyToMany
    @JoinTable(
            name = "user_favorites",
            joinColumns = [JoinColumn(name = "user_id")],
            inverseJoinColumns = [JoinColumn(name = "pet_id")])
    @JsonIgnoreProperties("likes")
    var favoritePets: MutableSet<Pet> = HashSet()

    fun likePet(pet: Pet) {
        favoritePets.add(pet)
    }

    fun unlikePet(pet: Pet) {
        favoritePets.remove(pet)
    }
}