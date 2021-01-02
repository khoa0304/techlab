$(document).ready(function() {
	var wordCountTable = $('#wordCountTable').DataTable({
		"sAjaxSource": "/table/wordcount",
		"sAjaxDataProp": "",
		"order": [[1, "desc"]],
		"aoColumns": [
			{ "mData": "word" },
			{ "mData": "count" }

		]
	});

	var sentenceAndWordCountTable = $('#sentenceAndWordCountTable').DataTable({
		"sAjaxSource": "/table/sentenceAndWordCount",
		"sAjaxDataProp": "",
		//"order": [[1, "desc"]],
		"aoColumns": [
			{ "mData": "fileName" },
			{ "mData": "totalWords" },
			{ "mData": "totalSentences" }

		]
	});

	
	// reloading the entire table, 
	setInterval(function() {
		wordCountTable.ajax.reload();
		sentenceAndWordCountTable.ajax.reload();
	}, 5000);


});

