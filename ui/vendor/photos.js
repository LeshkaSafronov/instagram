$('#upload-button').on("click", function (event) {
	$('.upload-photo-modal')
	  .modal('show')
	;
});


$('.container-photo').on("click", function (event) {

	$('.photo-modal').modal({
    autofocus: false
  }).modal('show');

})
