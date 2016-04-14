package it.polimi.se2.meteocal.entity;

import it.polimi.se2.meteocal.entity.Location;
import it.polimi.se2.meteocal.entity.User;
import it.polimi.se2.meteocal.entity.WForecast;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-02-19T17:39:57")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, String> password;
    public static volatile SingularAttribute<User, String> groupName;
    public static volatile CollectionAttribute<User, User> followedBy;
    public static volatile SingularAttribute<User, String> ResPassTime;
    public static volatile SingularAttribute<User, Boolean> hasPublicCalendar;
    public static volatile SingularAttribute<User, String> avatarString;
    public static volatile CollectionAttribute<User, User> follows;
    public static volatile SingularAttribute<User, WForecast> weather;
    public static volatile SingularAttribute<User, Location> location;
    public static volatile SingularAttribute<User, String> fullname;
    public static volatile SingularAttribute<User, String> type;
    public static volatile SingularAttribute<User, String> email;

}