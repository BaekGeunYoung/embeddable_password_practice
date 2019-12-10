package com.practice.embeddable_password.exception

import java.lang.RuntimeException

class PasswordNotMatchedException(val failedCount: Int) : RuntimeException()