package com.practice.embeddable_password.service.impl

import com.practice.embeddable_password.config.security.jwt.JwtTokenProvider
import com.practice.embeddable_password.dto.AccountDto
import com.practice.embeddable_password.entity.user.User
import com.practice.embeddable_password.repository.UserRepository
import com.practice.embeddable_password.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.security.sasl.AuthenticationException

@Service
class UserServiceImpl(
        @Autowired private val userRepository: UserRepository
) : UserService {
    override fun signUp(signUpDto: AccountDto.SignUpReq): User {
        val user = signUpDto.toEntity()
        return userRepository.save(user)
    }

    override fun signIn(signInDto: AccountDto.SignInReq, jwtTokenProvider: JwtTokenProvider): MutableMap<String, Any> {
        val user: User? = userRepository.findByUsername(signInDto.username)
        user?: throw UsernameNotFoundException("cannot find such user : ${signInDto.username}")

        if(user.password.isMatched(signInDto.rawPassword)){
            val token: String = jwtTokenProvider.createToken(signInDto.username, user.roles.map { it.name }.toList())

            val ret: MutableMap<String, Any> = HashMap()
            ret["username"] = signInDto.username
            ret["token"] = token

            return ret
        }
        else throw AuthenticationException()
    }
}