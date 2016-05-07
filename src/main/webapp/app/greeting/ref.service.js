$(function(){
    var animationend = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend';
    $("#greetins-dev").mouseenter(function(){
/*        $("#greeting").show();
        $(this).fadeOut();
        $("#input").fadeOut();*/
        $("#msg1").addClass('animated fadeInDown').one('animationend',function(){
            $(this).removeClass('animated fadeInDown');
        });
    })
})
