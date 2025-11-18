document.addEventListener("DOMContentLoaded", function() {
  const banner = document.getElementById("cookie-banner");
  const accepted = localStorage.getItem("cookiesAccepted");

  if (!accepted) {
    banner.classList.remove("d-none");
  }

  const acceptBtn = document.getElementById("accept-cookies");
  const rejectBtn = document.getElementById("reject-cookies");

  if (acceptBtn) {
    acceptBtn.addEventListener("click", function() {
      localStorage.setItem("cookiesAccepted", "true");
      banner.classList.add("d-none");
    });
  }

  if (rejectBtn) {
    rejectBtn.addEventListener("click", function() {
      localStorage.setItem("cookiesAccepted", "false");
      banner.classList.add("d-none");
    });
  }
});
