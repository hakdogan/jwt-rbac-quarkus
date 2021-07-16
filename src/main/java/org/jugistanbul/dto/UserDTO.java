package org.jugistanbul.dto;

import java.util.Objects;

/**
 * @author hakdogan (huseyin.akdogan@patikaglobal.com)
 * Created on 16.07.2021
 **/
public class UserDTO
{
    private String username;
    private String password;
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return username.equals(userDTO.username) && password.equals(userDTO.password) && role.equals(userDTO.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, role);
    }
}
