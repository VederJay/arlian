package org.arlian.site.model.user;

import org.arlian.site.model.start.page.Page;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class User implements UserIdProjection{

    /**
     * Short name the user likes to be called by.
     */
    private String givenName;

    /**
     * Full name of the user, consisting of first name and surname.
     */
    private String fullName;

    /**
     * Email address of the user.
     */
    private String emailAddress;

    /**
     * Url to a profile picture of the user.
     */
    private String pictureUrl;

    /**
     * Technical and meaningless ID serving as primary key in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name="user_id_seq",
            sequenceName = "user_id_seq", allocationSize = 5)
    private long id;

    /**
     * LocalDateTime of when the object was first persisted.
     */
    @CreationTimestamp
    private LocalDateTime createDateTime;

    /**
     * LocalDateTime of when the object was most recently updated.
     */
    @UpdateTimestamp
    private LocalDateTime updateDateTime;


    /********************
     * RELATED ENTITIES *
     ********************/

    @OneToMany(mappedBy = "user")
    private List<Page> pages;



    /****************
     * CONSTRUCTORS *
     ****************/

    public User(){}

    public User(String emailAddress, String givenName, String fullName, String pictureUrl){
        this.emailAddress = emailAddress;
        this.givenName = givenName;
        this.fullName = fullName;
        this.pictureUrl = pictureUrl;
    }


    /***********************
     * GETTERS AND SETTERS *
     ***********************/

    public long getId() {
        return id;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
