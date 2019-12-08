package com.project.recofashion.recofashion_app.config.security.userdetails

import com.practice.embeddable_password.entity.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails (private val user: User): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities = ArrayList<GrantedAuthority>()
        user.roles.map{ authorities.add(SimpleGrantedAuthority(it.name)) }

        return authorities
    }

    override fun getUsername() = user.username

    override fun getPassword() = user.password.value

    override fun isEnabled() = true

    override fun isCredentialsNonExpired() = true

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

}