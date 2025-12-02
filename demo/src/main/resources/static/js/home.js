function createBarChart(ctx, labels, values, bgColors, maxValue = null) {
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: '',
        data: values,
        backgroundColor: bgColors,
        borderRadius: 6
      }]
    },
    options: {
      indexAxis: 'y',
      responsive: true,
      plugins: {
        legend: { display: false },
        tooltip: { enabled: true }
      },
      scales: {
        x: {
          beginAtZero: true,
          max: maxValue,
          ticks: { color: '#ffffff', font: { weight: '500' } },
          grid: { color: 'rgba(255,255,255,0.1)' }
        },
        y: {
          ticks: { color: '#ffffff', font: { weight: '500' } },
          grid: { color: 'rgba(255,255,255,0.1)' }
        }
      }
    }
  });
}

// Paleta madera / cerveza
const beerColors = ['#F8D878','#E0A942','#C26A22','#6A3A1E','#2A1A0F'];

// Crear gráficos
const ctxValoradas = document.getElementById('chartTopValoradas').getContext('2d');
createBarChart(
  ctxValoradas,
  topValoradas.map(b => b.nombre),
  topValoradas.map(b => b.valor),
  beerColors.slice(0, topValoradas.length),
  5
);

const ctxConsumidas = document.getElementById('chartTopConsumidas').getContext('2d');
createBarChart(
  ctxConsumidas,
  topConsumidas.map(b => b.nombre),
  topConsumidas.map(b => b.valor),
  beerColors.slice(0, topConsumidas.length),
  Math.max(...topConsumidas.map(b => b.valor))
);

const ctxCalidad = document.getElementById('chartTopCalidad').getContext('2d');
createBarChart(
  ctxCalidad,
  topCalidad.map(b => b.nombre),
  topCalidad.map(b => b.valor),
  beerColors.slice(0, topCalidad.length),
  Math.max(...topCalidad.map(b => b.valor))
);
