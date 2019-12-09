package com.practice.embeddable_password.service.impl

import com.practice.embeddable_password.config.security.jwt.JwtTokenProvider
import com.practice.embeddable_password.dto.AccountDto
import com.practice.embeddable_password.entity.user.User
import com.practice.embeddable_password.repository.UserRepository
import com.practice.embeddable_password.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
        @Autowired private val userRepository: UserRepository,
        @Autowired private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : UserService {
    override fun signUp(signUpDto: AccountDto.SignUpReq): User {
        val user = signUpDto.toEntity(bCryptPasswordEncoder)
        return userRepository.save(user)
    }

    override fun signIn(signInDto: AccountDto.SignInReq, jwtTokenProvider: JwtTokenProvider, authenticationManager: AuthenticationManager): MutableMap<String, Any> {
        val user: User? = userRepository.findByUsername(signInDto.username)
        user?: throw UsernameNotFoundException("cannot find such user : ${signInDto.username}")

        try{
            val authenticator = UsernamePasswordAuthenticationToken(signInDto.username, signInDto.rawPassword)
            authenticationManager.authenticate(authenticator)

            user.password.updateFailedCount(true)
            val token: String = jwtTokenProvider.createToken(signInDto.username, user.roles.map { it.name }.toList())

            val ret: MutableMap<String, Any> = HashMap()
            ret["username"] = signInDto.username
            ret["token"] = token

            return ret
        }
        catch (e: AuthenticationException) {
            user.password.updateFailedCount(false)
            throw BadCredentialsException("invalid username/password supplied")
        }
    }
}