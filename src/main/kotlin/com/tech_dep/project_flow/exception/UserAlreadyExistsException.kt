package com.tech_dep.project_flow.exception

class UserAlreadyExistsException(email: String) : RuntimeException("User with email $email already exists")