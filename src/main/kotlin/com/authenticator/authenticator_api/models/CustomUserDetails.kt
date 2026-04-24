package com.authenticator.authenticator_api.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(val user: User) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${user.role?.name?.uppercase()}"))

    override fun getUsername() = user.username
    override fun getPassword() = user.hashedPassword
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = !user.banned
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = user.accountStatus.toString() == "APPROVED"
}


