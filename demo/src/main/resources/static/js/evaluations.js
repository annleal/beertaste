// evaluations.js
function openEvaluationModal() {
    const modalBody = document.getElementById('modalBody');
    modalBody.innerHTML = '<div class="text-center">Cargando...</div>';

    // Cargar formulario vía fetch
    fetch('/evaluaciones/nueva')
        .then(response => response.text())
        .then(html => {
            modalBody.innerHTML = html;
        })
        .catch(err => {
            modalBody.innerHTML = '<div class="text-danger text-center">Error al cargar el formulario.</div>';
            console.error(err);
        });

    const modal = new bootstrap.Modal(document.getElementById('evaluationModal'));
    modal.show();
}
