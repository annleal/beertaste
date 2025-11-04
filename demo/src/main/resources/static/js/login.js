document.addEventListener('DOMContentLoaded', function() {
  const form = document.querySelector('form');
  if (!form) return;

  form.addEventListener('submit', function(e) {
    const user = document.getElementById('username').value.trim();
    const pass = document.getElementById('password').value.trim();
    if (!user || !pass) {
      e.preventDefault();
      // mejor usar un mensaje más accesible que alert
      const existing = document.querySelector('.error-message');
      if (existing) {
        existing.textContent = 'Por favor, completa ambos campos.';
      } else {
        const div = document.createElement('div');
        div.className = 'error-message';
        div.textContent = 'Por favor, completa ambos campos.';
        div.setAttribute('role','alert');
        form.prepend(div);
      }
      return;
    }
  });
});
