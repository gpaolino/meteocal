function disableLocationForm(){
    
    alert(document.getElementById("signupForm:checked").checked);
    if(document.getElementById("signupForm:checked").checked){
        Init();
        document.getElementById("signupForm:address").disabled=true;
        document.getElementById("signupForm:address").style.backgroundColor = "#E7E7E7";
        document.getElementById("signupForm:street").disabled=true;
        document.getElementById("signupForm:street").style.backgroundColor = "#E7E7E7";
        document.getElementById("signupForm:number").disabled=true;
        document.getElementById("signupForm:number").style.backgroundColor = "#E7E7E7";
    }else{
        Init();
        document.getElementById("signupForm:address").disabled=false;
        document.getElementById("signupForm:address").style.backgroundColor = "white";
        document.getElementById("signupForm:street").disabled=false;
        document.getElementById("signupForm:street").style.backgroundColor = "white";
        document.getElementById("signupForm:number").disabled=false;
        document.getElementById("signupForm:number").style.backgroundColor = "white";
    }
}

function disableSocialFields(){
    if(!document.getElementById("socialCheck").checked){
        document.getElementById("signupForm:facebook").disabled=true;
        document.getElementById("signupForm:facebook").style.backgroundColor = "#E7E7E7";
        document.getElementById("signupForm:twitter").disabled=true;
        document.getElementById("signupForm:twitter").style.backgroundColor = "#E7E7E7";
    }else{
        document.getElementById("signupForm:facebook").disabled=false;
        document.getElementById("signupForm:facebook").style.backgroundColor = "white";
        document.getElementById("signupForm:twitter").disabled=false;
        document.getElementById("signupForm:twitter").style.backgroundColor = "white";
    }
}

function fillDescription(){
    
    var FAQ1on=document.getElementById("FAQ1").checked;
    var FAQ2on=document.getElementById("FAQ2").checked;
    var FAQ3on=document.getElementById("FAQ3").checked;
    var FAQ4on=document.getElementById("FAQ4").checked;
    
    if(FAQ1on | FAQ2on | FAQ3on | FAQ4on){
        document.getElementById("signupForm:editor").value +='\n\nFAQs\n\n';
    }
    if(FAQ1on){
        document.getElementById("signupForm:editor").value +='Are there ID requirements or an age limit to enter the event?\n **Insert the answer**\n\n';
    }
    if(FAQ2on){
        document.getElementById("signupForm:editor").value +='What are my transport/parking options getting to the event?\n **Insert the answer**\n\n ';
    }
    if(FAQ3on){
        document.getElementById("signupForm:editor").value +='What can/can t I bring to the event?\n **Insert the answer**\n\n ';
    }
    if(FAQ4on){
        document.getElementById("signupForm:editor").value +='Where can I contact the organiser with any questions?\n **Insert the answer**\n\n ';
    }
}

function openRepeatDiv(){ 
    
    //If the event a repeatable event I color the background
    document.getElementById("rep").style.display='block';
    document.getElementById("signupForm:startsAt_input").style.backgroundColor='#ffcb4c';
    document.getElementById("signupForm:endsAt_input").style.backgroundColor='#ffcb4c';
    document.getElementById("signupForm:repeatText").value="true";
}

function resetRepeatEvent(){
    
    document.getElementById("signupForm:startsAt_input").value="";
    document.getElementById("signupForm:startsAt_input").style.backgroundColor='white';
    document.getElementById("signupForm:endsAt_input").value="";
    document.getElementById("signupForm:endsAt_input").style.backgroundColor='white';
    document.getElementById("rep").style.display='none';
    document.getElementById("signupForm:repeatText").value="false";
}