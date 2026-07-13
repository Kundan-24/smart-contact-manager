console.log("contact_modal loaded");

const viewContactModal=document.getElementById('view_contact__modal')

const contactModal = new Modal(viewContactModal,{
    placement: 'center',
    backdrop: 'dynamic',
    closable: true
});


function openContactModal(){
    contactModal.show();
}

function closeContactModal(){
    contactModal.hide();
}

function fixUrl(url) {
    if (!url || url === "#" || url.trim() === "") return "#";
    return url.startsWith("http") ? url : "https://" + url;
}

 
async function loadContactdata(id){
    try {
        const response = await fetch(`/api/contacts/${id}`);
        const data = await response.json();

        console.log(data);

        // Info
        document.querySelector("#contact_name").innerText = data.name || "Unknown";
        document.querySelector("#contact_email").innerText = data.email || "No email";
        document.querySelector("#contact_phone").innerText = data.phoneNumber || "Not available";
        document.querySelector("#contact_address").innerText = data.address || "Not available";
        document.querySelector("#contact_description").innerText = data.description || "No description";

        // Image
        document.querySelector("#contact_image").src = data.picture || "https://static.vecteezy.com/system/resources/previews/018/765/757/original/user-profile-icon-in-flat-style-member-avatar-illustration-on-isolated-background-human-permission-sign-business-concept-vector.jpg";

        // Links
        document.querySelector("#contact_linkedin").href = fixUrl(data.linkedInLink);
        document.querySelector("#contact_website").href = fixUrl(data.websiteLink);

        // Favorite
        document.querySelector("#contact_favorite").innerText = data.favorite ? "⭐ Favorite Contact" : "";
        
        openContactModal();
    } catch (error) {
        console.log("Error loading contact: ", error);
    }
}


// delete contact
async function deleteContact(id) {

    const swalWithBootstrapButtons = Swal.mixin({
        customClass: {
            confirmButton: "bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded me-3",
            cancelButton: "bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded ms-3"
        },
        buttonsStyling: false
    });

    const result = await swalWithBootstrapButtons.fire({
        title: "Are you sure?",
        text: "You won't be able to revert this!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Yes, delete it!",
        cancelButtonText: "No, cancel!",
        reverseButtons: true
    });

    // AGAR USER DELETE PE CLICK KARE
    if (result.isConfirmed) {

        // SUCCESS MESSAGE
        await Swal.fire({
            title: "Deleted!",
            text: "Your contact has been deleted.",
            icon: "success",
            timer: 1500,
            showConfirmButton: false
        });

        // DELETE URL HIT
        window.location.href = `/user/contacts/delete/${id}`;
    }

    // AGAR CANCEL KARE
    else if (result.dismiss === Swal.DismissReason.cancel) {

        Swal.fire({
            title: "Cancelled",
            text: "Your contact is safe :)",
            icon: "error"
        });

    }
}