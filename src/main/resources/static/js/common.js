$(document).ready(function() {
	$("#LogoutLink").on("click", function(e) {
		e.preventDefault();
		document.LogoutForm.submit();
	});
});
