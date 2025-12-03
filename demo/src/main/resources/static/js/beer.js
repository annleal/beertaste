// ---------------- Modal ver foto ----------------
const photoModal = document.getElementById("photoModal");
const modalImg = document.getElementById("modalImage");

function showPhoto(id) {
    modalImg.src = "/beer/photo/" + id + "?t=" + new Date().getTime();
    photoModal.style.display = "block";
}

photoModal.querySelector(".custom-close").onclick = () => photoModal.style.display = "none";

window.onclick = (event) => {
    if (event.target === photoModal) photoModal.style.display = "none";
};

// ---------------- Preview imagen en beerModal ----------------
const photoInputModal = document.getElementById('photo');
const previewImgModal = document.getElementById('previewImgModal');

if(photoInputModal){
    photoInputModal.addEventListener('change', function(){
        const file = this.files[0];
        if(file){
            const reader = new FileReader();
            reader.onload = e => {
                previewImgModal.style.display = 'block';
                previewImgModal.src = e.target.result;
            }
            reader.readAsDataURL(file);
        } else {
            previewImgModal.style.display = 'none';
            previewImgModal.src = '';
        }
    });
}

// ---------------- Abrir modal para edición ----------------
function editBeer(btn) {
    const beer = {
        idCerveza: btn.dataset.id,
        businessName: btn.dataset.name,
        abv: btn.dataset.abv,
        country: { idCountry: btn.dataset.country },
        style: { styleId: btn.dataset.style },
        photoUrl: btn.dataset.hasphoto === "true" ? "/beer/photo/" + btn.dataset.id : null
    };

    // Abrir modal
    const beerModal = new bootstrap.Modal(document.getElementById('beerModal'));
    beerModal.show();

    // Rellenar campos
    document.getElementById('businessName').value = beer.businessName || '';
    document.getElementById('abv').value = beer.abv || '';
    
    // Seleccionar país
    const countrySelect = document.getElementById('country');
    if(countrySelect && beer.country) countrySelect.value = beer.country.idCountry;
    
    // Seleccionar estilo
    const styleSelect = document.getElementById('style');
    if(styleSelect && beer.style) styleSelect.value = beer.style.styleId;

    // Preview de foto
    if(previewImgModal){
        if(beer.photoUrl) {
            previewImgModal.style.display = 'block';
            previewImgModal.src = beer.photoUrl + "?t=" + new Date().getTime();
        } else {
            previewImgModal.style.display = 'none';
            previewImgModal.src = '';
        }
    }

    // Guardar idCerveza en hidden
    let hiddenId = document.getElementById('beerIdHidden');
    if(!hiddenId){
        hiddenId = document.createElement('input');
        hiddenId.type = 'hidden';
        hiddenId.id = 'beerIdHidden';
        hiddenId.name = 'idCerveza';
        document.getElementById('beerForm').appendChild(hiddenId);
    }
    hiddenId.value = beer.idCerveza || '';
}

// ---------------- Interceptar submit del formulario ----------------
const beerForm = document.getElementById('beerForm');

beerForm.addEventListener('submit', function(e) {
    e.preventDefault();

    const formData = new FormData();

    // Campos del formulario
    formData.append('businessName', document.getElementById('businessName').value);
    formData.append('abv', document.getElementById('abv').value);
    formData.append('country', document.getElementById('country').value);
    formData.append('style', document.getElementById('style').value);

    const photoInput = document.querySelector('#beerModal #photo');
    const file = photoInput?.files[0];

    // Solo agregar la foto si existe
    if(file instanceof File && file.size > 0){
        formData.append('photo', file);
    } 

    // Agregar id si es edición
    const beerId = document.getElementById('beerIdHidden')?.value;
    if(beerId) formData.append('idCerveza', beerId);

    const url = beerId ? '/beer/api/' + beerId : '/beer/api';
    const method = beerId ? 'PUT' : 'POST';

    fetch(url, { method, body: formData, credentials: 'same-origin' })
        .then(res => res.text())
        .then(msg => {
            alert(msg);
            const beerModal = bootstrap.Modal.getInstance(document.getElementById('beerModal'));
            beerModal.hide();
            beerForm.reset();
            const previewImg = document.getElementById('previewImgModal');
            if(previewImg){
                previewImg.style.display = 'none';
                previewImg.src = '';
            }
            if (document.getElementById('beerIdHidden')) document.getElementById('beerIdHidden').remove();
            location.reload();
        })
        .catch(err => {
            console.error(err);
            alert('Error al guardar la cerveza');
        });
});

