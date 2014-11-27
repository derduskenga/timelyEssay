$(document).ready(function(){
	makeLinkActive();
});
function makeLinkActive(){
	var path = location.pathname;
	$('#navigation >.list-group> a').each(function(e){
				if($(this).attr('href')==path){
						$(this).addClass("active");
				}
	});
}