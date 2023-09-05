	Dropzone.autoDiscover = false;

		$("#uploadFile").dropzone({

			maxFilesize : 4096,//MB
			timeout : 7200000,//milisecond - 2 hours
			acceptedFiles : ".jpeg,.jpg,.png,.gif,.pdf,.txt,.csv",

			success : function(file, response) {
				toastr.success('File ' + file.name + ' uploaded successfully');
			}

		});
