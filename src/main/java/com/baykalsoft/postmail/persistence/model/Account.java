package com.baykalsoft.postmail.persistence.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String verifyToken;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(nullable = false)
    private int status;

    public Account() {
    }

    public Account(String accessToken, Date created, String email, int status, String verifyToken) {
        this.accessToken = accessToken;
        this.created = created;
        this.email = email;
        this.status = status;
        this.verifyToken = verifyToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getVerifyToken() {
        return verifyToken;
    }

    public void setVerifyToken(String verifyToken) {
        this.verifyToken = verifyToken;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Account{");
        sb.append("accessToken='").append(accessToken).append('\'');
        sb.append(", id=").append(id);
        sb.append(", email='").append(email).append('\'');
        sb.append(", verifyToken='").append(verifyToken).append('\'');
        sb.append(", created=").append(created);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}