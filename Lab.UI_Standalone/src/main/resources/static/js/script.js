$(document).ready(function(){
	$pic = $('<img id = "image" width = "100%" height = "100%"/>');
	$("#photo").change(function(){
		var files = !!this.files ? this.files : [];
		if(!files.length || !window.FileReader){
			$("#image").remove();
			$lbl.appendTo("#preview");
		}
		if(/^image/.test(files[0].type)){
			var reader = new FileReader();
			reader.readAsDataURL(files[0]);
			reader.onloadend = function(){
				$pic.appendTo("#preview");
				$("#image").attr("src", this.result);
			}
		}
	});
});

$(function() {
  // Get the form element
  var form = $('#myForm');

  // Get the geolocation data
  var geolocation = navigator.geolocation.getCurrentPosition(function(position) {
    // Add the geolocation data to the form
    form.append('<input type="hidden" name="latitude" value="' + position.coords.latitude + '">');
    form.append('<input type="hidden" name="longitude" value="' + position.coords.longitude + '">');

    // Submit the form
    form.submit();
  });
});