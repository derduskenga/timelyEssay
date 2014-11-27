$(document).ready(function(){
	makeLinkActive();
	makeHomeLinkMenuActive();
});
function makeLinkActive(){
	var path = location.pathname;
	$('#navigation >.list-group> a').each(function(e){
				if($(this).attr('href')==path){
						$(this).addClass("active");
				}
	});
}

function makeHomeLinkMenuActive(){
	var path = location.pathname;
	$('#navigation > li > a').each(function(e){
				if($(this).attr('href')==path)
					$(this).parent().addClass("active");
	});
}