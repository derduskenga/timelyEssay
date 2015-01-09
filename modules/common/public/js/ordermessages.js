$(document).ready(function(event){
    
    var $area = $('.message-shared-class'); 
    
    $area.each(function(){ 
      var elemid = $(this).attr('id');
      $(this).waypoint(function(direction){
	if(direction === "up"){
	  var elementArray = elemid.split('-');
	  var type = elementArray[0]; //to or from
	  var order_code = elementArray[2];
	  var message_id = elementArray[3];
	  var status = elementArray[4]; //read or unread
	  
	  if(type == "to" && $("#" + elemid + "").hasClass("unread")){
	    $.post("/mydashboard/order/" + order_code + "/" + message_id,{}, function(data){
	      if(data['success'] == 1){
		//remove the unread class
		$("#" + elemid + "").removeClass("unread");
	      }
	    },'json');
	  }
	} 
      }, {
	offset:'75%'
      });
    });
    
    
    $area.each(function(){ 
      var elemid = $(this).attr('id');
      $(this).waypoint(function(direction){
	if(direction === "down"){
	  var elementArray = elemid.split('-');
	  var type = elementArray[0]; //to or from
	  var order_code = elementArray[2];
	  var message_id = elementArray[3];
	  var status = elementArray[4]; //read or unread
	  
	  if(type == "to" && $("#" + elemid + "").hasClass("unread")){
	    $.post("/mydashboard/order/" + order_code + "/" + message_id,{}, function(data){
	      if(data['success'] == 1){
		//remove the unread class
		$("#" + elemid + "").removeClass("unread");
	      }
	    },'json');
	  }
	} 
      }, {
	offset:'75%'
      });
    });
    
    
    
});