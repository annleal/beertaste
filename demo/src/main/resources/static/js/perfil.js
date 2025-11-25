document.addEventListener("DOMContentLoaded", () => {

    const userId = document.getElementById("userId").value;

    cargarPaises();
    cargarDatosUsuario(userId);

    document.getElementById("perfilForm").addEventListener("submit", (e) => {
        e.preventDefault();
        actualizarPerfil(userId);
    });
});

// ========================
// CARGAR PAÍSES
// ========================
function cargarPaises() {
    fetch("/api/countries")
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById("country");
            select.innerHTML = "";

            data.forEach(country => {
                let option = document.createElement("option");
                option.value = country.idCountry;
                option.textContent = country.name;
                select.appendChild(option);
            });
        });
}

// ========================
// CARGAR DATOS DEL USUARIO - 
// ========================
function cargarDatosUsuario(id) {
    fetch(`/api/users/${id}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById("country").value = data.country.idCountry;
        });
}

// ========================
// ACTUALIZAR PERFIL
// ========================
function actualizarPerfil(id) {

    const payload = {
        name: document.getElementById("name").value,
        surname: document.getElementById("surname").value,
        country: { idCountry: document.getElementById("country").value }
    };

    const password = document.getElementById("password").value.trim();
    if (password !== "") {
        payload.password = btoa(password);
    }

    fetch(`/api/users/${id}/profile`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
        .then(res => {
            if (!res.ok) throw new Error("Error actualizando perfil");
            return res.json();
        })
        .then(() => {
            alert("Perfil actualizado");
            document.getElementById("password").value = "";
        })
        .catch(err => alert("Error: " + err.message));
}
