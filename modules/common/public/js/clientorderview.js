$(document).ready(function(e){
  handleDownloadClick();
  handleProductFileDownload();
  setInterval(setClientLocalTime, 5000);
});

function handleDownloadClick(){
  $('.file-shared-class').click(function(e){
    e.preventDefault();
    var file_id = this.id.split("-")[2];
    window.location.href = "/mydashboard/order/file/downloadorderfile/" + file_id;
  });
}

function handleProductFileDownload(){
  $('.product-file-shared-class').click(function(e){
    e.preventDefault();
    var file_id = this.id.split("-")[2];
    window.location.href = "/mydashboard/order/file/downloadproductfile/" + file_id;
  });
}

function setClientLocalTime(){
  $('#file_upload_local_time').val(new Date().getTime());
}
