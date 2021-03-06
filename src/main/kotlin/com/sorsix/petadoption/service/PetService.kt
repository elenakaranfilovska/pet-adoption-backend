package com.sorsix.petadoption.service

import com.sorsix.petadoption.domain.Age
import com.sorsix.petadoption.domain.Pet
import com.sorsix.petadoption.domain.Sex
import com.sorsix.petadoption.domain.User
import com.sorsix.petadoption.domain.exception.InvalidPetIdException
import com.sorsix.petadoption.domain.exception.UnauthorizedException
import com.sorsix.petadoption.repository.PetRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PetService(val petRepository: PetRepository,
                 val contactService: ContactService,
                 val userService: UserService) {

    val logger: Logger = LoggerFactory.getLogger(PetService::class.java)

    fun findAll(page: Int?, size: Int?): Page<Pet> {
        val p = page ?: 0
        val s = size ?: 2
        val pageable: Pageable = PageRequest.of(p, s)
        return petRepository.findAllByOrderByTimestampDesc(pageable)
    }

    fun createPet(type: String, name: String, breed: String, color: String, age: Age, sex: Sex,
                  description: String, behaviour: String, image64Base: String?, weight: Double, height: Double,
                  allergies: String, vaccination: String,
                  email: String, firstName: String, lastName: String, address: String, city: String,
                  telephone: String): Pet {
        val contact = contactService.createContact(email, firstName, lastName, address, city, telephone)
        val user = userService.getCurrentUser()
        val pet = Pet(0, type, user, contact, name, breed, color, age, sex, description,
                behaviour, image64Base, weight, height, allergies, vaccination, LocalDateTime.now())
        logger.info("Saving pet [{}]", pet)
        return petRepository.save(pet)
    }

    fun deletePet(id: Long) {
        return this.petRepository.findById(id).map {
            val user = userService.getCurrentUser()
            if (user.username != it.owner.username)
                throw UnauthorizedException()
            for (u: User in userService.findAll()) {
                u.deleteFromFavourite(it)
                u.pets.remove(it)
            }
            logger.info("Deleting pet [{}]", it)
            petRepository.delete(it)
        }.orElseThrow {
            logger.error("Invalid pet id [{}]", id)
            throw InvalidPetIdException()
        }
    }

    fun getPetById(id: Long): Pet {
        return petRepository.findById(id).orElseThrow { throw InvalidPetIdException() }
    }

    fun editPet(id: Long, type: String, name: String, breed: String, color: String, age: Age, sex: Sex,
                description: String, behaviour: String, image64Base: String?, weight: Double, height: Double,
                allergies: String, vaccination: String,
                email: String, firstName: String, lastName: String, address: String, city: String,
                telephone: String): Pet {
        return this.petRepository.findById(id).map {
            val user = userService.getCurrentUser()
            if (user.username != it.owner.username)
                throw UnauthorizedException()
            val contact = contactService.createContact(email, firstName, lastName, address, city, telephone)
            val updated = Pet(id, type, user, contact, name, breed, color, age, sex, description,
                    behaviour, image64Base, weight, height, allergies, vaccination, LocalDateTime.now())
            logger.info("Updating pet with id [{}]", id)
            petRepository.save(updated)
            updated
        }.orElseThrow {
            logger.error("Invalid pet id [{}]", id)
            throw InvalidPetIdException()
        }
    }

}