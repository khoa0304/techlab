"use strict";

var src_Labels = [];
var src_Data = [];
var ctx = document.getElementById("bar-chart-horizontal");
var myChart = new Chart(ctx, {
	type: 'horizontalBar',
	data: {
		labels: src_Labels,
		datasets: [{
				label: '# of Mentions',
				data: src_Data,
		backgroundColor: [
			'rgba(255, 99, 132, 0.2)',
			'rgba(54, 162, 235, 0.2)',
			'rgba(255, 206, 86, 0.2)',
			'rgba(75, 192, 192, 0.2)',
			'rgba(153, 102, 255, 0.2)',
			'rgba(255, 159, 64, 0.2)',
			'rgba(255, 99, 132, 0.2)',
			'rgba(54, 162, 235, 0.2)',
			'rgba(255, 206, 86, 0.2)',
			'rgba(75, 192, 192, 0.2)',
			'rgba(153, 102, 255, 0.2)'
		],
		borderColor: [
			'rgba(255,99,132,1)',
			'rgba(54, 162, 235, 1)',
			'rgba(255, 206, 86, 1)',
			'rgba(75, 192, 192, 1)',
			'rgba(153, 102, 255, 1)',
			'rgba(255, 159, 64, 1)',
			'rgba(255,99,132,1)',
			'rgba(54, 162, 235, 1)',
			'rgba(255, 206, 86, 1)',
			'rgba(75, 192, 192, 1)',
			'rgba(153, 102, 255, 1)'
		],
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
			}
		}
   });


setInterval(function() {
	$.getJSON('/chart/refreshData', {
	}, function(data) {
		src_Labels = data.sLabel;
		src_Data = data.sData;
	});
	myChart.data.labels = src_Labels;
	myChart.data.datasets[0].data = src_Data;
	myChart.update();
}, 1000);