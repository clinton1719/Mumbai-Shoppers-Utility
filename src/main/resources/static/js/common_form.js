$(document)
	.ready(
		function() {
			$("#buttonCancel").on("click", function() {
				window.location = moduleUrl;
			});

			$("#formFile")
				.on(
					"change",
					function() {

						fileSize = this.files[0].size;

						if (fileSize > 1048576) {
							this
								.setCustomValidity("Kindly choose an image less than 1mb");
							this.reportValidity();
						} else {
							this
								.setCustomValidity("");
							changeThumbnail(this);
						}

					})
		});

function changeThumbnail(fileInput) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#thumbnail").attr("src", e.target.result);
	}
	reader.readAsDataURL(file);
}

function showWarningModal(title, message) {
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal('show');
}