//Add an element to the invitation list
function add(avatar, name, email) {
    
    email = email.trim().toLowerCase();
    //Check not already invited, not already added and not trying to add self
    if(invitationList.indexOf(email) != -1 || 
            alreadyInvited.indexOf(email) != -1 ||
            userEmail==email) {
        
        //Already invited
        if(alreadyInvited.indexOf(email) != -1) 
            addError("The user has already received an invitation for this event.");
           
        //Already added
        if(invitationList.indexOf(email) != -1)
            addError("The user is already in the list.");
        
        //Trying to invite self
        if(userEmail==email) 
            addError("You are already a partecipant! You don't need to invite yourself.");
     
        return;
        
    } 
    invitationList.push(email);
    
    var html = "<tr> \n\
               <td><img src=\""+avatar+"\" class='iconSmall'/></td> \n\
               <td>"+name+"</td> \n\
           </tr> ";
   if(invitationList.length === 1) $("#added").html("");
   $("#added").append(html);
   $("#searchResultsBoxInv").hide();
   $("#searchResultsBoxInv").value = "";
   
   //Update input hidden
   $("#invitationListHiddenWrapper").children().first().val(JSON.stringify(invitationList));
               
}

//Add error message
function  addError (msg) {
    $("#addError").show();
    $("#addErrorMessage").text(msg);
    $("#searchInputInvitation").val("");
    $("#searchResultsBoxInv").hide();
}


//Build an element of the result list
var invResultBuilder = function (id, fullname, avatar, city) {
    if(avatar==="") avatar="../resources/images/avatar_default.png";

    res = "<a onclick =\"add('"+avatar+"','"+fullname+"','"+id+"')\" style='color:black' >\n\
        <div class='row'> \n\
            <div class='col-md-2'>\n\
                <img src='"+avatar+"' class='avatarSmall'/> \n\
            </div>\n\
            <div class='col-md-10'>\n\
                <span class='searchResTextCont'>\n\
                    <h4 class='searchResName'>"+fullname+"</h4>\n\
                    <span class='searchResInfo'>"+city+"</span>\n\
                </span>\n\
            </div>\n\
        </div>\n\
       </a>";
    return res;
};

 
 $(document).ready(function(){
    //Setup invitation user search
    $("#searchInputInvitation").focusin(function(){$("#lensInv").addClass("lensActive");});
    $("#searchInputInvitation").focusout(function(){$("#lensInv").removeClass("lensActive");});
    $("#searchInputInvitation").keyup(handleKeyUp("#searchInputInvitation", "#searchResultsBoxInv", invResultBuilder));
    


    //Hide results if mouse out
    $(document).mouseup(function (e)
    {   $("#addError").hide( "fade");
        var container = $("#searchResultsBoxInv");

        if (!container.is(e.target) 
            && container.has(e.target).length === 0) 
        {   
            container.hide();
        }
    });                    
                    
});
