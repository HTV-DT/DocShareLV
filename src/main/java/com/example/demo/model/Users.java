package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        }),
        @UniqueConstraint(columnNames = {
                "email"
        })
}) 
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;
    @NotBlank
    @Size(min = 3,max = 50)
    private String username;
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    @NotBlank
    @Size(min = 6,max = 100)
    private String password;
    @Lob
    private String avatar;
    @Column(name = "verification_code", length = 64)
	private String verificationCode;
	private boolean enabled;
    
    private Double maxUpload;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> Files;

    @JsonIgnoreProperties("friends")
    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private Set<Users> friends = new HashSet<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,mappedBy="user",cascade = CascadeType.ALL)
    Set<Comment> comments;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,mappedBy="user",cascade = CascadeType.ALL)
    Set<Download> downloads;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,mappedBy="user",cascade = CascadeType.ALL)
    Set<Order> orders;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,mappedBy="user",cascade = CascadeType.ALL)
    Set<Like> likes;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,mappedBy="user",cascade = CascadeType.ALL)
    Set<Access> accesses;

    public Users() {
    }

    public Users(Long id, String name, String username, String email, String password, String avatar, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.roles = roles;
    }

    public Users( String name, String username,  String avatar) {
        this.name = name;
        this.username = username;
        this.avatar = avatar;
    }

    public Users(   @NotBlank
                    @Size(min = 3, max = 50) String name,
                    @NotBlank
                    @Size(min = 3,max = 50) String username,
                    @Size(max = 50)
                    @Email String email,
                    @NotBlank
                    @Size(min = 6,max = 100)String encode) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = encode;
    }
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    public String getVerificationCode() {
        return this.verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public List<File> getFiles() {
        return this.Files;
    }

    public void setFiles(List<File> Files) {
        this.Files = Files;
    }

    public Set<Users> getFriends() {
        
        return this.friends;
    }

    public void setFriends(Set<Users> friends) {
        this.friends = friends;
    }

    public void add(Users user) {
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Download> getDownloads() {
        return this.downloads;
    }

    public void setDownloads(Set<Download> downloads) {
        this.downloads = downloads;
    }

    public Set<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<Like> getLikes() {
        return this.likes;
    }

    public void setLikes(Set<Like> likes) {
        this.likes = likes;
    }

    public Set<Access> getAccesses() {
        return this.accesses;
    }

    public void setAccesses(Set<Access> accesses) {
        this.accesses = accesses;
    }


    public Double getMaxUpload() {
        return this.maxUpload;
    }

    public void setMaxUpload(Double maxUpload) {
        this.maxUpload = maxUpload;
    }

    public boolean hasFriendWithId(Long friendId) {
        for (Users friend : friends) {
            if (friend.getId().equals(friendId)) {
                return true;
            }
        }
        return false;
    }

}
