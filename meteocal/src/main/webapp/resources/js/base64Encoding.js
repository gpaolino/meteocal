function el(id){return document.getElementById(id);} // Get elem by ID

function simulateclick(){
    $("#readimg").click();    
}
function readImage() {
    var fileToLoad = document.getElementById("readimg").files[0];

    var fileReader = new FileReader();
    fileReader.onload = function(fileLoadedEvent) {
            var textFromFileLoaded = fileLoadedEvent.target.result;
            var previewimage = new Image();
            previewimage.id = "imagePreview";
            previewimage.src = textFromFileLoaded;
            previewimage.style.borderRadius = '4px';
            previewimage.style.width = '150px';
            previewimage.style.display = 'inline-block';
            previewimage.style.padding = '4px';
            previewimage.style.border = '1px solid #ddd';
            $("#getimage").html(previewimage);
            $("#getimage").append("<br/>");

            document.getElementById("signupForm:avatarBase64").value=textFromFileLoaded;
    };
    fileReader.readAsDataURL(fileToLoad);
}

$(document).ready(function(){
    
    //Per mascherare il bottone di default con uno 

    $("#readimg").css("visibility","collapse");
    $("#readimg").css("width", "0px");
    $("#openimage").click(function(){simulateclick(this);});
    $("#readimg").change(function(){readImage(this);});
 });