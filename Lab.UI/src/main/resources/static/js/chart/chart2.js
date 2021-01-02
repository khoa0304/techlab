"use strict";

var src_Labels = [];
var src_Data = [];
var ctx = document.getElementById("top-word-count-bar-chart-horizontal");
var myChart = new Chart(ctx, {
	type: 'horizontalBar',
	data: {
		labels: src_Labels,
		datasets: [{
				label: 'Word Count',
				data: src_Data,
				backgroundColor: poolColors(100),
		        borderColor:  poolColors(100),
			    borderWidth: 1
        	}]
    	},
		options: {
			scales: {
				xAxes: [{
					ticks: {
						beginAtZero: true,
						scaleBeginAtZero : true,
					}
				}]
			},
				
			legend: {
	         labels: {
	            generateLabels: function(chart) {
	               var labels = chart.data.labels;
	               var dataset = chart.data.datasets[0];
	               var legend = labels.map(function(label, index) {
	                  return {
	                     datasetIndex: 0,
	                     text: label,
	                     fillStyle: dataset.backgroundColor[index],
	                     strokeStyle: dataset.borderColor[index],
	                     lineWidth: 1
	                  }
	               });
	               return legend;
	            }
	         }
	      }
			
		}
   });


setInterval(function() {
	$.getJSON('/chart/getTopWordCount', {
	}, function(data) {
		src_Labels = data.sLabel;
		src_Data = data.sData;
	});
	myChart.data.labels = src_Labels;
	myChart.data.datasets[0].data = src_Data;
	myChart.update();
}, 3000);

function dynamicColors() {
    var r = Math.floor(Math.random() * 255);
    var g = Math.floor(Math.random() * 255);
    var b = Math.floor(Math.random() * 255);
    return "rgba(" + r + "," + g + "," + b + ", 0.5)";
}


function poolColors(a) {
    var pool = [];
    for(var i = 0; i < a; i++) {
        pool.push(dynamicColors());
    }
    return pool;
}