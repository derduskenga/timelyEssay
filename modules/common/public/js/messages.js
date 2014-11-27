$(document).ready(function(){
	
	$('#client-messages-form').bootstrapValidator({
		message: 'This value is not valid',
        live: 'disabled',
		feedbackIcons: {
            valid: '',
            invalid: 'fa fa-times',
            validating: ''
		},
		fields: {
		    msg_to: {
                validators: {
					notEmpty: {
                        message: 'Please select message receipient.'
                    }
                }
            },
           message: {
                validators: {
					notEmpty: {
                        message: 'Please type your message.'
                    }
                }
            }	
		}
	});

});

function alternateNewMessageView(panel){
		$('.messages-action').hide(1000);
		$('.new-msg-div').hide(1000);
		if(panel=="msg_action"){
			//$('.messages-action').removeClass('hidden');
			$(".messages-action").show(1000);
		}else{
			//$('.new-msg-div').removeClass('hidden');
			$('.new-msg-div').show(1000);
		}
}