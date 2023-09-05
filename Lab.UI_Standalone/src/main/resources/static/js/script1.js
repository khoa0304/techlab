
   

$(document).ready(function() {
  
  // Call the getGeolocation function and assign the data to the outside variable
	    getGeolocation(function (coordinates) {
	        geolocationData = coordinates;
	        // You can now use geolocationData as needed outside this scope
	        console.log("Latitude: " + geolocationData.latitude);
	        console.log("Longitude: " + geolocationData.longitude);
	    });

   // Initialize an outside variable to store geolocation
    var geolocationData;
	   $("#myButton").click(function () {
   
   		   getGeolocation();
          // Call the displayFeed function to load and display the feed
 		   displayFeed();
   
        });

    
        
    function getGeolocation(callback) {
        if ("geolocation" in navigator) {
            navigator.geolocation.getCurrentPosition(function (position) {
                var latitude = position.coords.latitude;
                var longitude = position.coords.longitude;
                var coordinates = {
                    latitude: latitude,
                    longitude: longitude
                };
                callback(coordinates);
            }, function (error) {
                switch (error.code) {
                    case error.PERMISSION_DENIED:
                        console.error("User denied the request for geolocation.");
                        break;
                    case error.POSITION_UNAVAILABLE:
                        console.error("Location information is unavailable.");
                        break;
                    case error.TIMEOUT:
                        console.error("The request to get user location timed out.");
                        break;
                    case error.UNKNOWN_ERROR:
                        console.error("An unknown error occurred.");
                        break;
                }
            });
        } else {
            console.error("Geolocation is not supported in this browser.");
        }
	}
    
    
      // URL to your JSON feed
    var feedUrl = "/api/news/feeds";
   
    // Function to fetch and display the feed
    function displayFeed() {
		
		
	    
	    var dataBody = JSON.stringify(geolocationData);
  
        $.ajax({
            url: feedUrl,
            method: "POST",
            dataType: "json",
            contentType: "application/json",
            data: dataBody,
            success: function(data) {
             
                // Clear existing content
                $("#feed-list").empty();

                // Loop through the articles and display them
                for (var i = 0; i < data.length; i++) {
                    var article = data[i];
                    var listItem = $("<li></li>");

                    // Create a link to the article
                    var articleLink = $("<a></a>");
                    articleLink.attr("href", article.url);
                    articleLink.text(article.title);

                    // Append the link to the list item
                    listItem.append(articleLink);

                    // Append the list item to the feed list
                    $("#feed-list").append(listItem);
                }
            },
            error: function() {
                console.log("Error fetching the feed.");
            }
        });
    }
    
    
    
});



