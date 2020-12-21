"use strict";

var src_Labels_chart_3 = [];
var src_Data_chart_3 = [];
var ctx_chart_3 = document.getElementById("sentence-word-count-bar-chart-horizontal");
var myChart_chart_3 = new Chart(ctx_chart_3, {
	type: 'horizontalBar',
	data: {
		labels: src_Labels_chart_3,
		datasets: [{
				label: 'Sentence Count',
				data: src_Data_chart_3,
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
	$.getJSON('/chart/getTotalSentenceAndWordCount', {
	}, function(data) {
		src_Labels_chart_3 = data.sLabel;
		src_Data_chart_3 = data.sData;
	});
	myChart_chart_3.data.labels = src_Labels_chart_3;
	myChart_chart_3.data.datasets[0].data = src_Data_chart_3;
	myChart_chart_3.update();
}, 1000);

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