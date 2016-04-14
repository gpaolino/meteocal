package it.polimi.se2.meteocal.entity;

import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.User;
import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-02-19T17:39:57")
@StaticMetamodel(Notification.class)
public class Notification_ { 

    public static volatile SingularAttribute<Notification, User> sentTo;
    public static volatile SingularAttribute<Notification, Boolean> isRead;
    public static volatile SingularAttribute<Notification, Event> aboutEvent;
    public static volatile SingularAttribute<Notification, Long> id;
    public static volatile SingularAttribute<Notification, String> type;
    public static volatile SingularAttribute<Notification, Timestamp> createdOn;

}