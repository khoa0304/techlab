"use strict";
var ctx = document.getElementById('chart').getContext('2d');
var w = 500, h = 500, maxR = 10, numItems = 500;

var chart = new Chart(ctx, {
  type: 'bubble',
  options: {
    legend: {
      display: false
    }
  },
  data: {
    datasets: [
      {
        data: [],
      }
    ]
  }
});



function getData(numItems) {
  let data = [];
  
  for(var i = 0; i < numItems; i++) {
    data.push({
      x: w * Math.random(),
      y: h * Math.random(),
      r: maxR * Math.random()
    });
  }
  
  return data;
}

function updateChart(data) {
  chart.data.datasets[0].data = data;
  chart.update();
}

updateChart(getData(numItems));

document.getElementById('num-items-input').addEventListener('change', function(e) {
  numItems = +e.target.value;
  updateChart(getData(numItems));
});

document.getElementById('update-button').addEventListener('click', function() {
  updateChart(getData(numItems));
});

