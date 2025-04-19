package com.tech_dep.project_flow.event

import com.tech_dep.project_flow.entity.User
import org.springframework.context.ApplicationEvent

data class RegistrationCompleteEvent(
    val user: User
) : ApplicationEvent(user)