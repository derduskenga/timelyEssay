$(document).ready(function(event){
  valididateRecoverPasswordForm();
});


function valididateRecoverPasswordForm(){
	    $('#recover_password_form').bootstrapValidator({
		    message: 'This value is not valid',
		    live: 'enabled',
		    
		    feedbackIcons: {
			valid: 'fa fa-check',
			invalid: 'fa fa-times',
			validating: 'fa fa-refresh'
		    },
	fields:{
  recover_password_email:{
    validators:{
      notEmpty:{
	message:'Field is required'
      },
      emailAddress:{
	message:'Enter a valid email'
      }
    }
  }
}
});
}