package com.tech_dep.project_flow.controller

import com.tech_dep.project_flow.dto.UserDto
import com.tech_dep.project_flow.enums.UserRole
import com.tech_dep.project_flow.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(val userService: UserService) {
    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") userId: UUID): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.getUser(userId))
    }

    @GetMapping("/me/role")
    fun getRole(
        @CookieValue(name = "accessToken") accessToken: String,
        @RequestParam projectId: UUID
    ): ResponseEntity<UserRole> {
        return ResponseEntity.ok(userService.getRole(accessToken, projectId))
    }
}