--MeteoCal MODEL
-------------------------------------
-- SIGNATURES

--Generic User identified by email address
abstract sig User {
	email: one String,
	invitations: set Invitation,
	notifications: set Notification
}
--Non Registered User
sig NotRegUser extends User {
}{ #invitations >= 1
   some inot: InvitationNot |  inot in notifications
}

--Registered User
sig RegUser extends User{
	emailConfirmed:  one String,
	------------------
	calendar: one Calendar,
	events: set Event, //owned events
	followed: set RegUser,
	followers: set RegUser,
}{ emailConfirmed = "true" || emailConfirmed =  "false"}

--Event
sig Event {
	owner: one RegUser,
	location: one Location,
	startsAt: one Int,
	endsAt: one Int,
	notifTrigger: one Int, //0 = SUN |  1=CLOUD | 2 = RAIN | 3 = THUNDERSTORM | 4= SNOW
	canceled: one String,
	invitations: set Invitation,
	comments: set Comment
}{
	startsAt >= 0 && endsAt >= 0
	canceled = "true" || canceled = "false" 
	notifTrigger >= 0 && notifTrigger=< 4
}

--Comment
sig Comment {
	author: one RegUser,
	toEvent: one Event
}

--Calendar
sig Calendar {
	owner: one RegUser,
	events: set Event
}

--Location
sig Location {
	isIndoor: one String,
	weatherForecast: one WeatherForecast
}{ isIndoor = "true" || isIndoor = "false" }

--WeatherForecast
sig WeatherForecast{
	value: Int,
	toString: String
} { (value=0 && toString="SUN") ||
    (value=1 && toString="CLOUDS") ||
    (value=2 && toString="RAIN") ||
	(value=3 && toString="THUNDERSTORM") ||
	(value=4 && toString="STORM") 
}

--Notification
abstract sig Notification {
	relatedToEvent: one Event,
	addressedTo: one User
}

--Notification of weather changes
sig WeatherChangedNot extends Notification {}

--OtherChanged: event in which the user was invited was canceled/modified
sig NotMyEventChangedNot extends Notification {}

--InvitationNot
sig InvitationNot extends Notification {}

--Invitation
sig Invitation {
	status: one String,
	sentTo: one User,
	toEvent: one Event
}{ status="ACCEPTED" ||
   status="DECLINED" ||
   status="PENDING" ||
   status="CANCELED"
}

-------------------------------------
--PREDICATES

//Force good weather and event not canceled
pred goodWeatherNotCanceled [] {
	#WeatherChangedNot = 0 
	all e:Event | e.canceled = "false" && e.notifTrigger = 1
}

//WORLD1: A NRU is invited
pred world1 [] {
	goodWeatherNotCanceled []

	#NotRegUser = 1
	#Comment  = 0
}	

// WORLD2: NRU invites another NRU, NRU follows NRU, invited NRU leaves a comment
pred world2 [] {

	all u:User | u.email = "user1@me.com" || u.email = "user2@me.com"
	goodWeatherNotCanceled []

	#User = 2
	#Comment = 1
	#Event = 1
	#Invitation = 1
	#NotMyEventChangedNot = 0

	some u1,u2:User | u1.followers = u2
	all e:Event | e.comments.author != e.owner

}

//WORLD3: Declined invitation
pred world3 [] {
	all u:User | u.email = "user1@me.com" || u.email = "user2@me.com"
	goodWeatherNotCanceled []

	#User = 2
	some u:User | u.invitations.status  = "DECLINED"
	
}

//WORLD4: Multiple events, multiple users
pred world4 [] {
	
	#User = 2
	#Event = 3
	#Notification = 0
	#Invitation = 0
	#Comment = 0
	
	all u:User | #u.events >= 1
	
}


//WORLD5: WeatherChanged, owner and guest notified
pred world5[] {
	#User = 2
	#Invitation = 1
	#WeatherChangedNot > 1
	#Comment = 0	
}

//True iff the set of followed users, after have followed a new user, is equal to the 
//previous set plus the user just followed 
pred followFriend[ru1,ru1',ru2:RegUser]{
	ru1'.followed=ru1.followed+ru2
	ru1'.followers=ru1.followers
	ru1'.emailConfirmed=ru1.emailConfirmed
	ru1'.calendar=ru1.calendar
	ru1'.events=ru1.events
}

//True iff the set of followed users, after have unfollowed a new user, is equal to the 
//previous set minus the user just followed
pred unfollowFriend[ru1,ru1',ru2:RegUser]{
	ru1'.followed=ru1.followed-ru2
	ru1'.followers=ru1.followers
	ru1'.emailConfirmed=ru1.emailConfirmed
	ru1'.calendar=ru1.calendar
	ru1'.events=ru1.events
}

//True iff the set of events in the user's calendar and set of user's events, after have added a new event, are equal to the 
//previous sets plus the event just created  
pred addEvent[ru1,ru1':RegUser,e:Event]{
	ru1'.followed=ru1.followed
	ru1'.followers=ru1.followers
	ru1'.emailConfirmed=ru1.emailConfirmed
	ru1'.calendar.events=ru1.calendar.events+e
	ru1'.events=ru1.events+e
}
//True iff the set of events in the user's calendar, after have canceled a new event, are equal to the 
//previous sets minus the event just created. Instead, the number of user's events set remains equal, the only thing that 
//changes is the attribute canceled that switches from "no" to "yes"
pred cancelEvent[ru1,ru1':RegUser,e:Event]{
	ru1'.followed=ru1.followed
	ru1'.followers=ru1.followers
	ru1'.emailConfirmed=ru1.emailConfirmed
	ru1'.calendar.events=ru1.calendar.events-e
	ru1'.events=ru1.events
	ru1'.events.canceled="yes"
}

-------------------------------------
--FACTS (22)

//RU and NRU  complete the set Users
fact userSet {
	User = NotRegUser + RegUser
}

//Not two users with the same email
fact  emailUnique {
	not some disj u1,u2:User | u1.email = u2.email
}

//Consistency of relation invitation<->user
fact invitationUserConsistency {
	all i:Invitation, u:User | i.sentTo = u iff i in u.invitations
}

//Consistency of relation notification<->user
fact notificationUserConsistency {
	all n:Notification, u:User | n in u.notifications iff n.addressedTo = u
}

//Consistency of relation RegUser<->Event
fact eventUserConsistency {
	all u:RegUser, e:Event | e in u.events iff e.owner = u
}

//Consistency of relation RegUser <-> Calendar
fact calendarUserConsistency {
	all u:RegUser, c:Calendar | u.calendar = c iff c.owner = u
}

//Consistency or relation Event<->Invitations
fact invitationEventConsistency {
	all i:Invitation, e:Event | i.toEvent = e iff i in e.invitations
}

//Consistency or relation Comments<->Events
fact commentsEventsConsistency {
	all e:Event, c:Comment | c in e.comments iff e in c.toEvent
}

//Followers/Followers
fact followingRules {
	all disj u1,u2: RegUser | u2 in u1.followed  iff u1 in u2.followers
	not some u: RegUser | u in u.followed 
	not some u: RegUser | u in u.followers
}

//A NRU or a RU with not accepted email can have only PENDING or CANCELED invitations
fact notFullyRegisteredConstraints {
  all u:NotRegUser, i:Invitation | i in u.invitations implies (i.status = "PENDING" || i.status= "CANCELED")
  all u:RegUser, i:Invitation |
		 (i in u.invitations && u.emailConfirmed = "false")  implies (i.status= "PENDING" || i.status= "CANCELED")
}

//A RU  with not confirmed email has not own events and no events in his calendar
fact notAcceptedEmail {
	all u:RegUser | u.emailConfirmed = "false" implies #u.events = 0
}

//Constraints on Calendar
fact calendarContents {
	//Calendar contains all not canceled owned events
	all e:Event, u:RegUser | (e in u.events && e.canceled = "false") implies
		e in u.calendar.events
	//Calendar contains all events to which the user is invited with status ACCEPTED
	all i:Invitation, u:RegUser | (i.sentTo=u && i.status = "ACCEPTED" )  implies
		i.toEvent in u.calendar.events
	//Calendar does not contain any other event
	not some e:Event, u:User |
		( e in u.calendar.events &&
			(e not in u.events && e not in u.invitations.toEvent)
		) 
	not some e:Event, u:User, i:Invitation|
		e in u.calendar.events && i.toEvent = e && i.sentTo = u&& i.status != "ACCEPTED"
	
	not some e:Event, u:User | e in u.calendar.events && e.canceled = "true"
}


//A user cannot invite itself to one of his events
fact notSelfInvitation {
	all e:Event | e.owner not in e.invitations.sentTo
}

//If the event is canceled all the invitations must have CANCELED as status
fact canceledEvent {
	all e:Event, i:Invitation | (i.toEvent = e && e.canceled="true") implies
		 i.status = "CANCELED"
	all e:Event, i:Invitation | (i.toEvent = e &&  i.status = "CANCELED") implies
		e.canceled = "true"
}

//A notification can be related to events owned by the user or that have the user as guest with status = PENDING | ACCEPTED | CANCELED
fact notificationEventDomain {
	all n:Notification, u:User, i:Invitation | 
		( n in u.notifications implies (
			( n.relatedToEvent in u.events  ) || (n.relatedToEvent in u.invitations.toEvent)
	    	)
		) && (
			(n.relatedToEvent = i.toEvent && i in u.invitations) implies
				( i.status="PENDING" ||  i.status ="ACCEPTED" ||  i.status ="CANCELED")
		)
}

//Not two invitations for the same event to the same user
fact invitationUnique {
 	all disj  i1,i2:Invitation | (i1.sentTo = i2.sentTo) implies i1.toEvent !=i2.toEvent
}

//Invitation notifications constraints
fact invitationNotConstraints {
	//Exists the related invitation
	all inot: InvitationNot | inot.relatedToEvent in inot.addressedTo.invitations.toEvent 

	//Not two invitation notifications for the same event
	all disj inot1,inot2 :InvitationNot | inot1.relatedToEvent != inot2.relatedToEvent

}

//Weather notifications can exist only if (EVENT IS OUTDOOR)  && (ACTUALWEATHER equal or worse NOTIFTRIGGER)
fact weatherNotExistence {
	all w:WeatherChangedNot 
		| w.relatedToEvent. location.weatherForecast .value>=  w.relatedToEvent.notifTrigger
		&& w.relatedToEvent.location.isIndoor = "true"
	//Reverse condition
	all e:Event | e.notifTrigger  =< e.location.weatherForecast.value implies (some n:  WeatherChangedNot | n.relatedToEvent = e)
}

//NOTE1: We should say that there must be as many WeatherChangedEvents as are the times the weather status
//				goes above trigger .. but we should store the weather changes somewhere and this would add unuseful 
//				complexity to the model.

//NotMyEventChangedNot only for events to which the user is invited
fact notMyEventNots {
	all n: NotMyEventChangedNot | n.relatedToEvent in n.addressedTo.invitations.toEvent
}

//NOTE2: We should say that there must be as many NotMyEventChangedNot as are the times the event date/location
//			   change. We don't do it for the same reason of NOTE1

//NOTE3: We should also say that if a WeatherChangedNotification or a NotMyEventChangedNot is sent to one guest
//			   all other guests with not declined invitation should receive the same notification.
//			   We could also write complex constraints on the number of notifications received by two guests of the same event
//			   In order to simplify we only write the folllowing 'weak' constraints:
fact notificationsConstr {
	//If a user has a weather notification for an event e, the owner and all the other guests of the event should have at least 
	//one weather notification regarding the same event
	all u:User, n:WeatherChangedNot, e:Event |(n in u.notifications && u.notifications.relatedToEvent = e 
	&& e.canceled = "false") implies (
			all u:User | (u in e.invitations.sentTo || u = e.owner) implies 
					(some t:WeatherChangedNot| t.addressedTo = u && e in t.relatedToEvent)
	)

	//If a user has a NotMyEventChanged notification for an event e, all the other guests should have at least 
	//one NotMyEventChanged notification regarding the same event
	all u:User, n:NotMyEventChangedNot, e:Event |(n in u.notifications && u.notifications.relatedToEvent = e 
	&& e.canceled = "false") implies (
			all u:User | u in e.invitations.sentTo  implies 
					(some t:NotMyEventChangedNot| t.addressedTo = u && e in t.relatedToEvent)
	)
}

//Time constraints
fact timeConstraints {
	//Positive duration of events
	all e:Event | e.endsAt > e.startsAt

	all disj e1,e2:Event,  u: RegUser| (e1 in u.calendar.events && e2 in u.calendar.events) implies 
				not (
				( e2.startsAt >= e1.startsAt && e2.startsAt  <e1.endsAt) ||
				( e2.endsAt  > e1.startsAt  && e2.endsAt  <= e1.endsAt )
				)
	//The constraint above allows an event to start in the same instant when another event finishes
}

//Only guests and owner of an event can comment that event
fact commentsAuthorsConstraint {
	all c:Comment | c.author in c.toEvent.invitations.sentTo || c.author in c.toEvent.owner
}

//Not independent  Location | WeatherForecast (To avoid atoms not envolved in a relation)
fact notDateTimeAndLocationIndependent {
	all l:Location | l in Event.location
	all wf:WeatherForecast | (wf in Location.weatherForecast )
}

-------------------------------------
--ASSERTIONS 

//run world1 for 2
//run world2 for 2
//run world3 for 2
//run world4 for 10
//run world5 for 3

//Two users can't own the same event
assert ownerTest {
	not some e: Event, disj u1,u2:RegUser | e in u1.events && e in u2.events
}
//check ownerTest for 5

//If a User does not confirm his email he cannot have any event in his calendar
assert calVoid {
	not some u:RegUser | u.emailConfirmed = "false" && #u.calendar.events > 0
}
//check calVoid

//A User cannot have an invitation notification related to one of his events
assert invitationNotification {
	not some u:RegUser, n:InvitationNot | n in u.notifications && n.relatedToEvent.owner = u
}
//check invitationNotification

//When I follow and the unfollow a user the state doesn't change
assert followUnfollowUserDoesNotChangeState{
	all ru1,ru2,ru1',ru1'': RegUser | ru2 not in ru1.followed and followFriend[ru1,ru1',ru2] 
and
	unfollowFriend[ru1,ru1',ru2] implies ru1.followed = ru1''.followed
}
//check followUnfollowUserDoesNotChangeState

//When I create an event and then I delete the event, the calendar does not change. Instead, in the sets of user events the added event
// will be kept, but its attribute canceled is switched from "no" to "yes"
assert addCancelEventDoesNotChangeState{
	all ru1,ru1',ru1'': RegUser,e:Event | e not in (ru1.calendar.events) and ru1.events.canceled="yes" and addEvent[ru1,ru1',e] 
and
	cancelEvent[ru1,ru1',e] implies ru1.calendar.events = ru1''.calendar.events
}
//check addCancelEventDoesNotChangeState

//check if a user can invite both register and not register users
assert inviteBothRegisteredAndNotRegistered{
	all i:Invitation | i.sentTo in NotRegUser || i.sentTo in RegUser 
}

check inviteBothRegisteredAndNotRegistered
