$('body').layout({
	applyDefaultStyles: true
});
$('#inner').layout({
	applyDefaultStyles: true
});


function myFunction() {

	$
		.post(
			"/streaming/startstop",
			function(data) {
				//As soon as the browser finished downloading, this function is called.
				$('#demo').html(data);

				var buttontext = document
					.getElementById("streamingButton").innerHTML;
				if (buttontext === 'Start Streaming In Progress')
					document.getElementById("streamingButton").innerHTML = "Stop Streaming";
				else {
					document.getElementById("streamingButton").innerHTML = "Start Streaming";
				}
			});

	var buttontext = document.getElementById("streamingButton").innerHTML;
	if (buttontext === 'Start Streaming')
		document.getElementById("streamingButton").innerHTML = "Stop Streaming In Progress";
	else {
		document.getElementById("streamingButton").innerHTML = "Start Streaming In Progress";
	}
}

