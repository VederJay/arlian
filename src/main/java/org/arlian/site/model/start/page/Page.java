package org.arlian.site.model.start.page;

import org.arlian.site.model.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Page {

    /**
     * Technical and meaningless ID serving as primary key in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "page_id_seq")
    @SequenceGenerator(name="page_id_seq",
            sequenceName = "page_id_seq", allocationSize = 5)
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


    /**
     * Name of the page.
     */
    private String name;

    /**
     * Indicates whether the page is the default page for the user, i.e. the first one shown
     * when the user logs on.
     */
    private boolean isDefault;


    /********************
     * RELATED ENTITIES *
     ********************/

    /**
     * The user to which this page belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


    /****************
     * CONSTRUCTORS *
     ****************/

    public Page(){}

    public Page(User user, String name, boolean isDefault){
        this.name = name;
        this.isDefault = isDefault;
        this.user = user;
    }


    /***********************
     * GETTERS AND SETTERS *
     ***********************/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
