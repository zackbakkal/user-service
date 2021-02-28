package com.zack.peojects.chatapp.userservice.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String username;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean isOnline;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private String availability;
    private String profileImageName;

}
