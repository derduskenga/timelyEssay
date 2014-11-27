$(document).ready(function(){
		//$('.client-friend-mail-invitation-div').hide();
});

function alternateInvationMailView(panel){	
		$('.client-friend-mail-invitation-div').hide(1000);
		$('.email-invitation-div').hide(1000);
		if(panel=="btn_action"){
			//$('.messages-action').removeClass('hidden');
			$(".client-friend-mail-invitation-div").show(1000);
		}else{
			//$('.new-msg-div').removeClass('hidden');
			$('.email-invitation-div').show(1000);
		}
} 
