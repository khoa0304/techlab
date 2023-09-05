$(document).ready(function () {
    var modal = $("#myModal");
   
   // Initialize an outside variable to store geolocation
   var geolocationData = {
	   	latitude :  'a',
		longitude : 'a'
   };
    
    $("#openModalButton").click(function () {
        modal.css("display", "block");
    });

    $(".close").click(function () {
        modal.css("display", "none");
    });

    // Close the modal if the user clicks outside of it
    $(window).click(function (event) {
        if (event.target == modal[0]) {
            modal.css("display", "none");
        }
    });
    
     

    // Prevent the form from closing the modal when submitted
    $("#myForm").submit(function (event) {
        event.preventDefault();
        
     
	   // Call the getGeolocation function and assign the data to the outside variable
	    getGeolocation(function (coordinates) {
	        geolocationData = coordinates;
	        // You can now use geolocationData as needed outside this scope
	        console.log("Latitude: " + geolocationData.latitude);
	        console.log("Longitude: " + geolocationData.longitude);
	         // Call the displayFeed function to load and display the feed
	   		addFeed();
	    });
		    
       
        
        
    });
    
    // Function to fetch and display the feed
    function addFeed() {
		
		
          // Get form data
        var formData = {
            text: $("#text").val(),
            email: $("#email").val()
    	};

		$.extend(formData, geolocationData);
	    
	    var dataBody = JSON.stringify(formData);
  
        $.ajax({
            url: "/feeds/add",
            method: "POST",
            dataType: "json",
            contentType: "application/json",
            data: dataBody,
            success: function(data) {
             
                console.log("Feed is added"); 
            }
    
		})// end ajax
	}// end displayFeed()
    
    
    
         
});
