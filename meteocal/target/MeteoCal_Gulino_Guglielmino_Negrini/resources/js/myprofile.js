/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Actions performed on page load.
 * @param {type} param
 */
$( document ).ready(function() {
    
    //Draw privacy toggle switch
    $("#privacySetForm\\:publicCalBool").val(isPublic);
    if(isPublic){
        $("#publicCalDiv").html('<input type="checkbox" id="publicCalChk" name="publicCalChk" checked="checked"/>');
    }else{
        $("#publicCalDiv").html('<input type="checkbox" id="publicCalChk" name="publicCalChk" />');
    }
    $("#deleteAccountForm\\:delAccBool").val(false);

    //Show the user the first section of profile page
    showSection(0);

    //Loading Toggle Switches
    $("[name='publicCalChk']").bootstrapSwitch();
    $("[name='delAccountChk']").bootstrapSwitch();

    $('input[name="publicCalChk"]').on('switchChange.bootstrapSwitch', function(event, state) {
    $("#privacySetForm\\:publicCalBool").val(state);
    });
    $('input[name="delAccountChk"]').on('switchChange.bootstrapSwitch', function(event, state) {
    $("#deleteAccountForm\\:delAccBool").val(state);
    });
    
    showMap('signupForm','map-canvas','address','','','lat_long');
});

/**
 * Changes the page section visualized.
 * @param {type} i
 * @returns {undefined}
 */
function showSection(i) {
   $(activeItem).removeClass("active");
   $("#btn"+i).addClass("active");
   activeItem = "#btn"+i;

   $(activeSection).hide();
   $("#sec"+i).show();
   activeSection = "#sec"+i;
}
