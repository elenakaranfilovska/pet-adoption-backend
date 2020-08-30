package com.sorsix.petadoption.service

import com.sorsix.petadoption.domain.Contact
import com.sorsix.petadoption.domain.Pet
import com.sorsix.petadoption.domain.User
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.io.File
import javax.mail.internet.InternetAddress

@Service
class EmailService(val mailSender: JavaMailSender) {

    fun sendEmail(pet: Pet, receiver: Contact, adopter: User) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setTo(InternetAddress(receiver.email))
        helper.setSubject("Notification from PetFriends")
        helper.setFrom("adoptapetapp@gmail.com")
        helper.setText(createMessageContent(pet, receiver, adopter), false)
        val logo = FileSystemResource(File("static/logosmall.png"))
        helper.addInline("logo", logo)
        mailSender.send(message)
    }

    private fun createMessageContent(pet: Pet, receiver: Contact, adopter: User): String {
        val message = StringBuilder()
        message.append("Dear ").append(receiver.firstName).append(" ").append(receiver.lastName).append(",\n")
                .append("We would like to inform you that ").append(adopter.username)
                .append(" is interested about your pet ").append(pet.name).append(".\n")
                .append("You can contact this user on this email ").append(adopter.email).append(".\n")
                .append("Warm regards from team of PetFriends")
        return message.toString()
    }
}