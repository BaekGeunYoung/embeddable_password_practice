package com.practice.embeddable_password.entity.user

import javax.persistence.*

@Entity
data class User(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(name="username", unique = true, length = 200)
    var username: String,

    var roles: MutableSet<Role>,

    @Embedded
    var password: Password
)