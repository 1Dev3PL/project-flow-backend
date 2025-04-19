package com.tech_dep.project_flow.listener

import com.tech_dep.project_flow.event.RegistrationCompleteEvent
import com.tech_dep.project_flow.utils.VerificationTokenUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class RegistrationListener(
    private val mailSender: JavaMailSender,
    private val verificationTokenUtils: VerificationTokenUtils,
    @Value("\${tech_dep.app.clientUrl}")
    private val clientUrl: String,
) {
    @EventListener
    @Async
    fun handleRegistrationEvent(event: RegistrationCompleteEvent) {
        val verificationToken = verificationTokenUtils.generateVerificationToken(event.user)
        val confirmationUrl = clientUrl + "/confirmation?token=" + verificationToken.token
        val message = "Перейдите по ссылке для подтверждения аккаунта. Ссылка действительна в течение 6 часов"

        val email = SimpleMailMessage()
        email.setTo(event.user.email)
        email.subject = "Подтверждение регистрации"
        email.text = "$message\r\n$confirmationUrl"
        mailSender.send(email)
    }
}