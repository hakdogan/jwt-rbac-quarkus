package org.jugistanbul.entity;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.*;
import org.jugistanbul.dto.UserDTO;

import javax.persistence.*;
import java.util.List;

/**
 * @author hakdogan (huseyin.akdogan@patikaglobal.com)
 * Created on 16.07.2021
 **/
@Entity
@Table(name = "user_t")
@UserDefinition
public class User extends PanacheEntity
{
    @Username
    public String username;

    @Password
    public String password;

    @Roles
    public String role;

    public static User findByUsername(final String username){
        return find("username", username).firstResult();
    }

    public static List<User> findByRole(final String role){
        return find("role", role).list();
    }

    public static User add(final UserDTO dto) {
        var user = new User();
        user.username = dto.getUsername();
        user.password = BcryptUtil.bcryptHash(dto.getPassword());
        user.role = dto.getRole();
        user.persist();

        return user;
    }
}
