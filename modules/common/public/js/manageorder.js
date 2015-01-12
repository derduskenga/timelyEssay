$(document).ready(function(){
  validateUploadFile();
  askDeadlineExtensionAdmin();
  askForExtraPages();
  
  submitAskForExtraPages();
  
  jQuery('#order_deadline_admin').datetimepicker({
    format:'Y-m-d H:i'
  });
  
});

function submitAskForExtraPages(){
  $('#extra-pages-btn').click(function(event){
    event.preventDefault();
    var pages = $('#extra_pages').val();
    var order_code = $('#order-code-admin').text();
    $.post("/manageorder/askforextrapages/" + pages + "/" + order_code,{}, function(data){
       //A NEW TOTAL RETURNED HERE SHOULD UPLDATE THE CURRENT LABEL
    },'json');
  });
}

function askForExtraPages(){
      $('#extra_pages_form').bootstrapValidator({
	      message: 'This value is not valid',
      live: 'disabled',
	      feedbackIcons: {
	  valid: '',
	  invalid: 'fa fa-times',
	  validating: ''
	      },
	      fields: {
		  extra_pages: {
	      validators: {
		notEmpty: {
		      message: 'Extra pages required. Please enter a number'
		  },
		  integer:{
		    message:'An integer required'
		  }
	      }
	    }
	    
	  }
      });
} 

function askDeadlineExtensionAdmin(){
  
          $('#extend_deadline_form_admin').bootstrapValidator({
	      message: 'This value is not valid',
	      live: 'disabled',
	      feedbackIcons: {
	  valid: '',
	  invalid: 'fa fa-times',
	  validating: ''
	      },
	      fields: {
		  order_deadline_admin: {
	      validators: {
				      notEmpty: {
		      message: 'New date and time required.'
		  },
		  date:{
		    format:'YYYY-MM-DD h:m',
		    message:'Wrong date and time format'
		  }
	      }
	  },
	  deadline_extension_reason:{
	    validators:{
	      notEmpty:{
		message:'Please select the reason for deadline extension'
	      }
	    }
	  }
	      }
      }); 
  
}

function validateUploadFile(){
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
}