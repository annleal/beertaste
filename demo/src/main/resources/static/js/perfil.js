document.addEventListener("DOMContentLoaded", () => {
    cargarPaises()
        .then(() => cargarDatosUsuario());

    document.getElementById("perfilForm").addEventListener("submit", (e) => {
        e.preventDefault();
        actualizarPerfil();
    });
});

// ========================
// CARGAR PAÍSES
// ========================
function cargarPaises() {
    return fetch("/api/countries")
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
// CARGAR DATOS DEL USUARIO LOGUEADO
// ========================
function cargarDatosUsuario() {
    fetch("/api/users/current")
        .then(res => {
            if (!res.ok) throw new Error("No hay usuario logueado");
            return res.json();
        })
        .then(user => {
            document.getElementById("name").value = user.name;
            document.getElementById("surname").value = user.surname;
            document.getElementById("email").value = user.email;
            document.getElementById("country").value = user.country.idCountry;

            // Guardamos id del usuario en el formulario
            document.getElementById("perfilForm").dataset.userId = user.id;
        })
        .catch(err => {
            alert(err.message);
            window.location.href = "/login";
        });
}

// ========================
// ACTUALIZAR PERFIL
// ========================
function actualizarPerfil() {
    const form = document.getElementById("perfilForm");
    const id = form.dataset.userId;
    const name = document.getElementById("name").value;
    const surname = document.getElementById("surname").value;
    const password = document.getElementById("password").value;
    const country = document.getElementById("country").value;

    let payload = {
        name,
        surname,
        country: { idCountry: country }
    };

    if (password.trim() !== "") {
        payload.password = btoa(password);
    }

    fetch(`/api/users/${id}/profile`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
        .then(res => {
            if (!res.ok) throw new Error("Error al actualizar el perfil");
            return res.json();
        })
        .then(() => {
            alert("Perfil actualizado correctamente");
            document.getElementById("password").value = "";
        })
        .catch(err => alert("Error: " + err.message));
}
