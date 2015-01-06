$(document).ready(function(){
	$('#orderFileForm').bootstrapValidator({
		message: 'This value is not valid',
        live: 'disabled',
		feedbackIcons: {
            valid: '',
            invalid: 'fa fa-times',
            validating: ''
		},
		fields: {
		    product_file_type: {
                validators: {
					notEmpty: {
                        message: 'Select product type.'
                    }
                }
            }
		}
	});
});