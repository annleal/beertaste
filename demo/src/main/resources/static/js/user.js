const userTableBody = document.getElementById("userTableBody");
const userModal = new bootstrap.Modal(document.getElementById("userModal"));
const userForm = document.getElementById("userForm");

// ---------------- Load Users ----------------
let currentPage = 0;
const pageSize = 10;

async function loadUsers(page = 0) {
    const res = await fetch(`/admin/api/users?page=${page}&size=${pageSize}`);
    const data = await res.json();

    userTableBody.innerHTML = "";
    data.content.forEach(u => {
        userTableBody.innerHTML += `
            <tr>
                <td>${u.name}</td>
                <td>${u.surname}</td>
                <td>${u.email}</td>
                <td>${u.role}</td>
                <td>
                    <button class="btn btn-warning btn-sm" onclick="editUser(${u.idUser})">Editar</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteUser(${u.idUser})">Eliminar</button>
                </td>
            </tr>
        `;
    });

    renderPagination(data.totalPages, page);
}

function renderPagination(totalPages, page) {
    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    // Botón "Anterior"
    const prevBtn = document.createElement("button");
    prevBtn.className = "btn btn-sm btn-secondary me-1";
    prevBtn.innerText = "« Anterior";
    prevBtn.disabled = page === 0;
    prevBtn.onclick = () => loadUsers(page - 1);
    pagination.appendChild(prevBtn);

    // Botón "Siguiente"
    const nextBtn = document.createElement("button");
    nextBtn.className = "btn btn-sm btn-secondary";
    nextBtn.innerText = "Siguiente »";
    nextBtn.disabled = page >= totalPages - 1;
    nextBtn.onclick = () => loadUsers(page + 1);
    pagination.appendChild(nextBtn);

    currentPage = page;
}

// ---------------- Open Modal for CREATE ----------------
function openCreateModal() {
    document.getElementById("idUser").value = "";
    userForm.reset();
    userModal.show();
}

// ---------------- Open Modal for EDIT ----------------
async function editUser(id) {
    const res = await fetch("/admin/api/users/" + id);
    const u = await res.json();

    document.getElementById("idUser").value = u.idUser;
    document.getElementById("name").value = u.name;
    document.getElementById("surname").value = u.surname;
    document.getElementById("email").value = u.email;
    document.getElementById("country").value = u.country.idCountry; // oculto
    document.getElementById("role").value = u.role;
    document.getElementById("password").value = "";

    userModal.show();
}

// ---------------- Delete User ----------------
async function deleteUser(id) {
    if (!confirm("¿Seguro que deseas eliminar este usuario?")) return;
    await fetch("/admin/api/users/" + id, { method: "DELETE" });
    loadUsers(currentPage);
}

// ---------------- Submit Form (POST / PUT) ----------------
userForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const id = document.getElementById("idUser").value;
    const formData = new FormData();
    formData.append("name", document.getElementById("name").value);
    formData.append("surname", document.getElementById("surname").value);
    formData.append("email", document.getElementById("email").value);
    formData.append("country", document.getElementById("country").value); // oculto
    formData.append("role", document.getElementById("role").value);

    const password = document.getElementById("password").value;
    if (password.trim() !== "") formData.append("password", password);

    const method = id ? "PUT" : "POST";
    const url = id ? `/admin/api/users/${id}` : "/admin/api/users";

    await fetch(url, { method, body: formData });
    userModal.hide();
    loadUsers(currentPage);
});

// ---------------- Init ----------------
loadUsers();
