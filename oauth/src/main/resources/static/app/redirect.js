$(document).ready(function (){
    try{
        const arrMsg = ["로딩중.", "로딩중..", "로딩중...", "로딩중...."];
        let index = 0;
        let timer = setInterval(function(){
            let message = arrMsg[index];

            $('#description').text(message);

            index++;
            if(index === arrMsg.length){
                index = 0
            }
        },500);

        let redirectUrl = $('#redirectUrl').val();
        console.log(redirectUrl)
        setTimeout(function(){
            location.href = encodeURI(redirectUrl);
            return false;
        },500);

        setTimeout(function(){
            sendError(redirectUrl, '');
            alert("연결이 지연되고 있습니다.\n잠시 후 재시도 해주시거나, 고객센터로 문의해주세요.");
            $('#description').text("-");
            clearInterval(timer);
            return false;
        },(1000*20));
    }catch(e){
        sendError(e)
    }

    function sendError(redirectUrl, e){
        $.ajax({
            url:'/error/log',
            type:'POST',
            data:  JSON.stringify({
                source:'신한카드 인증페이지',
                userAgent: (window.navigator.userAgent),
                redirectUrl : redirectUrl,
                description : e.toString()
            }),
            contentType: "application/json; charset=utf-8",
            dataType:'json',
        });
    }
});
