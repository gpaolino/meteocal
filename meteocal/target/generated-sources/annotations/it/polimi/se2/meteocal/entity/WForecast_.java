package it.polimi.se2.meteocal.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-02-19T17:39:57")
@StaticMetamodel(WForecast.class)
public class WForecast_ { 

    public static volatile SingularAttribute<WForecast, String> weather;
    public static volatile SingularAttribute<WForecast, Float> temperature;
    public static volatile SingularAttribute<WForecast, Float> humidity;
    public static volatile SingularAttribute<WForecast, Long> id;

}