document.addEventListener('DOMContentLoaded', () => {
  const beerTable = document.getElementById('beerTableClient');
  const LOAD_API = '/beertap/load';

  function formatPrice(v) { return v ? Number(v).toFixed(2) + '€' : '-'; }
  function renderStars(n) { n = Math.max(0, Math.min(5, Math.round(n||0))); return '★'.repeat(n) + '☆'.repeat(5-n); }

  async function loadTapData() {
    try {
      const res = await fetch(LOAD_API);
      const tapList = await res.json();

      beerTable.innerHTML = '';
      if(!tapList.length){
        beerTable.innerHTML = `<tr><td colspan="8" class="text-muted">No hay grifos disponibles.</td></tr>`;
        return;
      }

      tapList.forEach(t => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td class="text-warning">${t.tapNumber||'-'}</td>
          <td>${t.name||'-'}<div style="font-size:0.85rem;color:#ccc">${t.country||''}</div></td>
          <td>${t.styleName||'-'}</td>
          <td>${t.styleColor||'-'}</td>
          <td>${t.abv??'-'}</td>
          <td>${formatPrice(t.priceHalfPint)}</td>
          <td>${formatPrice(t.pricePint)}</td>
          <td class="text-warning">${renderStars(t.avgRating)}</td>`;
        beerTable.appendChild(tr);
      });

    } catch(e) {
      console.error(e);
      beerTable.innerHTML = `<tr><td colspan="8" class="text-danger">Error cargando grifos.</td></tr>`;
    }
  }

  // Carga inicial
  loadTapData();

  // Auto-recarga cada 5 minutos (300000 ms)
  setInterval(loadTapData, 300000);
});
