//REQUIRES JQUERY and BOOTSTRAP
//Set the text with local weather information
function setWeather(string) {
    $('#weather').html(string);
}
    
//This function handles event keyup on search input text
var handleKeyUp = function(inputID,boxID, resBuilder) {
    
    inpt = $(inputID);
    box = $(boxID);
    return function(e) {
        
        if($(inputID).val().length>0){
            $(boxID).show();
            getResults(inpt.val(), boxID, resBuilder);
        } else {
            box.hide();
        }
    };
};  

//Build a single notification row
//todo: target address
function buildNot(notID, evID, title, description, date, read) {
    clss = "list-group-item";
    if(read==="false") 
        clss += " blueTrasp active";
    ntf = "<a href='event.xhtml?id="+evID+"&deleteNotifId="+notID+"' class='"+clss+"'> \n\
             <h4 class='list-group-item-heading'>"+title+"</h4> \n\
             <span class='notDate'>"+date.substring(0,16)+"</span>\n\
             <p class='list-group-item-text'>"+description+"</p \n\
           </a>";
    return ntf;
}

//Get notifications. max to be displayed
function getNotifications(limited, destination, badge) {
    
    $.getJSON( "../NotifServlet", function( data ) {
        var count = 0; 
        var max = 5;
        
       
        unread = 0;
        
        $.each(data, function(arrayID,ntf) {
            if(count == 0)  $(destination).html("");
            if(limited==true && count>=max) return; else count++;
            if(ntf.read === "false") unread++; count++;
            
            not = buildNot(ntf.notID, ntf.evID, ntf.title, ntf.text,ntf.createdOn,ntf.read);
            $(destination).append(not);
            
        }); 
        
        $(badge).text(unread);
    });
      
}

function resSelected(name) {
     $("#searchInputNav").val(name);
}


//Build a single search result item in the navBar
var navResultBuilder = function buildRes(id, fullname, avatar, city) {
    if(avatar==="") avatar="../resources/images/avatar_default.png";
    res = "<a href='users.xhtml?email="+id+"' onmouseover='resSelected(\""+fullname+"\")' style='color:black' >\n\
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
            
//Get results
function getResults(query, boxID, builder) {
    $.getJSON( "/meteocal/search?q="+query, function( data ) {
        
        $(boxID).html("");
        
        if(data.length === 0) {
            $(boxID).html("<p style='padding:15px'>No results found.</p>");
        }
        
        $.each(data, function(arrayID,res) {
            rs = builder(res.id, res.fullname, res.avatar,res.city);
            $(boxID).append(rs);
            
        });
        
    });
}

function simpleInterface() {
    $('#meteoBox').remove();
    
    $('#extraSpace').addClass('extraSpace');
    $('#breadcrumbs').addClass('breadcrumbFixed');
    $('#breadcrumbs').css("background-color","rgba(245, 245, 245, 0.74)");

}


$(document).ready(function(){
    getNotifications(true, "#notList", "#messageBadge");
    
    //Setup navigation user search
    $("#searchInputNav").focusin(function(){$("#lensNav").addClass("lensActive");});
    $("#searchInputNav").focusout(function(){$("#lensNav").removeClass("lensActive");});
    $("#searchInputNav").keyup(handleKeyUp("#searchInputNav", "#searchResultsBoxNav", navResultBuilder));
    
    
    //Hide results on mouseout
    $(document).mouseup(function (e)
    {
        var container = $("#searchResultsBoxNav");

        if (!container.is(e.target) // if the target of the click isn't the container...
            && container.has(e.target).length === 0) // ... nor a descendant of the container
        {   
            container.hide();
        }
    });
    
       /*$(window).scroll(function() {
        if( $(window).scrollTop() >= 126) {
        
        }; 
    });*/
    
             
});