console.log("admin user");

document.addEventListener("DOMContentLoaded", function(){
document.querySelector("#image_file_input").addEventListener('change', function(event){
    let file = event.target.files[0];

    if(file){
    let reader = new FileReader();
    reader.onload = function(){
        document.querySelector("#upload_image_preview").setAttribute("src", reader.result);
    };
    reader.readAsDataURL(file);
    }
  });
});