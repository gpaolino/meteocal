package it.polimi.se2.meteocal.entity;

import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-02-19T17:39:57")
@StaticMetamodel(Invitation.class)
public class Invitation_ { 

    public static volatile SingularAttribute<Invitation, String> text;
    public static volatile SingularAttribute<Invitation, User> invitedUser;
    public static volatile SingularAttribute<Invitation, Event> toEvent;
    public static volatile SingularAttribute<Invitation, String> status;

}