document.addEventListener("DOMContentLoaded", function() {
    const photoInput = document.getElementById("photoInput");
    const previewImg = document.getElementById("previewImg");

    photoInput.addEventListener("change", function(evt) {
        const file = evt.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = function(e) {
            previewImg.src = e.target.result;
            previewImg.style.display = "block";
        };
        reader.readAsDataURL(file);
    });
});
