$(document).ready(function(e){
  handleDownloadClick();
  handleProductFileDownload();
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
