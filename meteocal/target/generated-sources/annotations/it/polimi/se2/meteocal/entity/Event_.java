package it.polimi.se2.meteocal.entity;

import it.polimi.se2.meteocal.entity.Location;
import it.polimi.se2.meteocal.entity.User;
import it.polimi.se2.meteocal.entity.WForecast;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-02-19T17:39:57")
@StaticMetamodel(Event.class)
public class Event_ { 

    public static volatile SingularAttribute<Event, Boolean> isCanceled;
    public static volatile SingularAttribute<Event, WForecast> weatherForecast;
    public static volatile SingularAttribute<Event, Integer> notIfTrigger;
    public static volatile SingularAttribute<Event, String> description;
    public static volatile SingularAttribute<Event, String> facebookLink;
    public static volatile SingularAttribute<Event, Boolean> isOutdoor;
    public static volatile SingularAttribute<Event, String> name;
    public static volatile SingularAttribute<Event, String> twitterLink;
    public static volatile SingularAttribute<Event, Date> startsAt;
    public static volatile SingularAttribute<Event, Boolean> isPublic;
    public static volatile SingularAttribute<Event, Location> location;
    public static volatile SingularAttribute<Event, Long> id;
    public static volatile SingularAttribute<Event, Date> endsAt;
    public static volatile SingularAttribute<Event, User> user;

}