$(document).ready(function () {
    var isClose = $("#isClose").val()
    if(isClose == 'true'){
        alert("잘못된 접근입니다.")
        window.webViewFinish()
    }
});
