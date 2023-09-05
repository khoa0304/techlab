$(function() {
		$("#searchBox")
				.autocomplete(
						{
							source : function(request, response) {
								$
										.ajax({
											url : "http://12.8.0.2:11515/search/getTags?querytext="
													+ request.term + "",
											dataType : "json",
											minLength : 2,
											data : {
												q : request.term
											},
											success : function(data) {

												response($.map(data, function(
														item) {
													return {
														label : item.tagName,
														value : item.tagName
													};
												}));
											},

											error : function(data) {
												console.log('search error');
											}
										});
							},

							select : function(event, ui) {
								$("#result").html(ui.item.value).show();
							}

						})

	});