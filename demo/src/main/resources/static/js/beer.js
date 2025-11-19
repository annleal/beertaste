function showPhoto(id) {
    const modal = document.getElementById("photoModal");
    const modalImg = document.getElementById("modalImage");

    // Loading spinner opcional
    modalImg.src = "";
    modalImg.alt = "Cargando...";
    
    modal.style.display = "block";
    modalImg.src = "/beer/photo/" + id + "?t=" + new Date().getTime();
    modalImg.onload = () => { modalImg.alt = ""; };
}

document.querySelector(".close").onclick = function() {
    document.getElementById("photoModal").style.display = "none";
};

window.onclick = function(event) {
    const modal = document.getElementById("photoModal");
    if (event.target === modal) {
        modal.style.display = "none";
    }
};
