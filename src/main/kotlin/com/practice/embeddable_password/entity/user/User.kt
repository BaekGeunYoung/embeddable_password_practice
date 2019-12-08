package com.practice.embeddable_password.entity.user

import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.GeneratedValue
import javax.persistence.Id

data class User(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(name="username", unique = true, length = 200)
    var username: String,

    var roles: MutableSet<Role>,

    @Embedded
    var password: Password
)