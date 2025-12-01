document.addEventListener('DOMContentLoaded', () => {

  const beerSearchInput = document.getElementById('beerSearchInput');
  const beerSuggestions = document.getElementById('beerSuggestions');
  const beerAbv = document.getElementById('beerAbv');
  const beerStyle = document.getElementById('beerStyle');
  const beerColor = document.getElementById('beerColor');
  const beerRating = document.getElementById('beerRating');
  const beerHalf = document.getElementById('beerHalf');
  const beerPint = document.getElementById('beerPint');
  const beerTapNumber = document.getElementById('beerTapNumber');
  const addOrUpdateBtn = document.getElementById('addOrUpdateBtn');
  const addOrUpdateLabel = document.getElementById('addOrUpdateLabel');
  const clearFormBtn = document.getElementById('clearFormBtn');
  const beerTable = document.getElementById('beerTable');
  const saveBtn = document.getElementById('saveBtn');
  const reloadBtn = document.getElementById('reloadBtn');
  const openClientView = document.getElementById('openClientView');

  let beersIndex = {};
  let tapList = [];
  let editingIndex = -1;
  let allBeers = [];

  const SEARCH_API = '/beer/api/search';
  const LOAD_API = '/beertap/load';
  const SAVE_API = '/beertap/save';
  const CLIENT_VIEW = '/beertap/view';

  if(openClientView) openClientView.href = CLIENT_VIEW;

  function formatPrice(v) { return v ? Number(v).toFixed(2) + '€' : '-'; }
  function renderStars(n) { n = Math.max(0, Math.min(5, Math.round(n||0))); return '★'.repeat(n) + '☆'.repeat(5-n); }

  function setFormFromBeer(beer) {
    beerSearchInput.value = beer.name || '';
    beerAbv.value = beer.abv ?? '';
    beerStyle.value = beer.styleName || '';
    beerColor.value = beer.styleColor || '';
    beerRating.textContent = renderStars(beer.avgRating);
    beerHalf.value = beer.priceHalfPint ?? '';
    beerPint.value = beer.pricePint ?? '';
    beerTapNumber.value = beer.tapNumber ?? '';
  }

  function clearForm() {
    beerSearchInput.value = '';
    beerAbv.value = '';
    beerStyle.value = '';
    beerColor.value = '';
    beerRating.textContent = '—';
    beerHalf.value = '';
    beerPint.value = '';
    beerTapNumber.value = '';
    editingIndex = -1;
    addOrUpdateLabel.textContent = 'Añadir';
  }

  async function loadTapData() {
    try {
      const res = await fetch(LOAD_API);
      const data = await res.json();
      tapList = Array.isArray(data) ? data.map(t => ({
        id: t.id ?? null,
        name: t.name ?? '',
        abv: t.abv ?? null,
        styleName: t.styleName ?? '',
        styleColor: t.styleColor ?? '',
        country: t.country ?? '',
        avgRating: t.avgRating ?? 0,
        priceHalfPint: t.priceHalfPint ?? 0,
        pricePint: t.pricePint ?? 0,
        tapNumber: t.tapNumber ?? ''
      })) : [];
      renderTapList();
    } catch(e) {
      console.error(e);
      tapList = [];
      renderTapList();
    }
  }

  async function saveTapData() {
    try {
      const res = await fetch(SAVE_API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(tapList)
      });
      if(!res.ok) throw new Error('Error guardando JSON');
      saveBtn.classList.add('btn-success');
      setTimeout(()=> saveBtn.classList.remove('btn-success'), 800);
    } catch(e) { console.error(e); alert('Error guardando'); }
  }

  function renderTapList() {
    beerTable.innerHTML = '';
    if(!tapList.length) { beerTable.innerHTML = `<tr><td colspan="9" class="text-muted">No hay grifos configurados.</td></tr>`; return; }

    tapList.forEach((t, idx) => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td class="text-warning"><strong>${t.tapNumber||'-'}</strong></td>
        <td><strong>${t.name||'-'}</strong><div style="font-size:0.85rem;color:#ccc">${t.country||''}</div></td>
        <td>${t.styleName||'-'}</td>
        <td>${t.styleColor||'-'}</td>
        <td>${t.abv??'-'}</td>
        <td>${formatPrice(t.priceHalfPint)}</td>
        <td>${formatPrice(t.pricePint)}</td>
        <td class="text-warning">${renderStars(t.avgRating)}</td>
        <td>
          <div class="d-flex gap-2 justify-content-end">
            <button class="btn btn-sm btn-outline-light edit-btn" data-idx="${idx}"><i class="bi bi-pencil"></i></button>
            <button class="btn btn-sm btn-outline-danger delete-btn" data-idx="${idx}"><i class="bi bi-trash"></i></button>
          </div>
        </td>`;
      beerTable.appendChild(tr);
    });

    document.querySelectorAll('.edit-btn').forEach(btn => btn.addEventListener('click',()=> onEditTap(Number(btn.dataset.idx))));
    document.querySelectorAll('.delete-btn').forEach(btn => btn.addEventListener('click',()=> {
      const idx = Number(btn.dataset.idx);
      if(!confirm('¿Eliminar este grifo?')) return;
      tapList.splice(idx,1);
      renderTapList();
    }));
  }

  function onEditTap(idx) { setFormFromBeer(tapList[idx]); editingIndex=idx; addOrUpdateLabel.textContent='Actualizar'; window.scrollTo({top:0, behavior:'smooth'}); }

  // --- Buscador dinámico ---
  beerSearchInput.addEventListener('input', async () => {
    const query = beerSearchInput.value.trim();
    if(query.length < 1) { beerSuggestions.innerHTML=''; return; }

    try {
      const res = await fetch(`${SEARCH_API}?q=${encodeURIComponent(query)}`);
      const results = await res.json();
      allBeers = results;
      beerSuggestions.innerHTML = results.length ? results.map(b => `<div class="suggestion-item" data-id="${b.id}">${b.name}</div>`).join('') : `<div class="no-results">Sin resultados</div>`;
    } catch(e) { console.error(e); }
  });

  beerSuggestions.addEventListener('click', e => {
    if(e.target.classList.contains('suggestion-item')){
      const beer = allBeers.find(b => b.id==e.target.dataset.id);
      if(beer){ setFormFromBeer(beer); beerSuggestions.innerHTML=''; }
    }
  });

  addOrUpdateBtn.addEventListener('click', (ev)=> {
  ev.preventDefault();
  if(!beerSearchInput.value.trim()){ alert('Selecciona una cerveza'); return; }

  const selectedBeer = allBeers.find(b => b.name === beerSearchInput.value);

  const entry = {
    id: selectedBeer?.id ?? null,
    name: beerSearchInput.value,
    abv: Number(beerAbv.value)||null,
    styleName: beerStyle.value,
    styleColor: beerColor.value,
    country: '',
    avgRating: selectedBeer?.avgRating ?? 0, // <-- aquí el cambio
    priceHalfPint: Number(beerHalf.value||0),
    pricePint: Number(beerPint.value||0),
    tapNumber: beerTapNumber.value||''
  };

  if(editingIndex>=0){
    tapList[editingIndex]=entry;
    editingIndex=-1;
    addOrUpdateLabel.textContent='Añadir';
  } else {
    if(entry.tapNumber && tapList.find(t=>t.tapNumber===entry.tapNumber))
      if(!confirm(`Ya existe grifo "${entry.tapNumber}". Añadir de todos modos?`)) return;
    tapList.push(entry);
  }
  clearForm();
  renderTapList();
});


  clearFormBtn.addEventListener('click', ev=>{ ev.preventDefault(); clearForm(); });
  saveBtn.addEventListener('click', async ev=>{ ev.preventDefault(); await saveTapData(); });
  reloadBtn.addEventListener('click', async ev=>{ ev.preventDefault(); if(!confirm('Recargar JSON descartará cambios?')) return; await loadTapData(); });

  loadTapData();
});
