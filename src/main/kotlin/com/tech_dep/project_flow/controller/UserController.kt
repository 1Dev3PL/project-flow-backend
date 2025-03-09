package com.tech_dep.project_flow.controller

import com.tech_dep.project_flow.dto.UserDto
import com.tech_dep.project_flow.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(val userService: UserService) {
    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") userId: UUID): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.getUser(userId))
    }
}