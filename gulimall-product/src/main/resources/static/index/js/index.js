$(function() {
    $(".header_head_p").mouseenter(function() {
        $(this).find(".header_head_p_cs").css("display", "block");
    });
    $(".header_head_p").mouseleave(function() {
        $(this).find(".header_head_p_cs").css("display", "none");
    });

    $(".header_gw").mouseenter(function() {
        $(this).find(".header_ko").css("display", "block");
    });
    $(".header_gw").mouseleave(function() {
        $(this).find(".header_ko").css("display", "none");
    });

    var mySwiper = new Swiper('.swiper-container', {
        autoplay: true,
        loop: true,
        pagination: {
            el: '.swiper-pagination',
        },
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
    });

    var leftSwiper = new Swiper('.swiper_section_second_list_left', {
        slidesPerView: 1,
        loop: true,
        autoplay: true,
        pagination: {
            el: '.section_second_list_right_button',
            clickable: true,
            renderBullet: function(index, className) {
                return '<p class="' + className + '"></p>';
            },
        },
    });

    var rightSwiper = new Swiper('.swiper_section_second_list_right', {
        slidesPerView: 1,
        loop: true,
        autoplay: true,
    });

    leftSwiper.controller.control = rightSwiper;
    rightSwiper.controller.control = leftSwiper;

    $(".section_second_list_right_button p").mouseenter(function() {
        var index = $(this).index();
        leftSwiper.slideTo(index);
        rightSwiper.slideTo(index);
        $(".section_second_list_right_button p").removeClass("section_second_list_right_button_active");
        $(this).addClass("section_second_list_right_button_active");
    });

    $(".left_floor li").click(function() {
        var index = $(this).index();
        var floorTop = $(".floor").eq(index).offset().top;
        $("html,body").animate({scrollTop: floorTop}, 500);
    });

    $(window).scroll(function() {
        var scrollTop = $(window).scrollTop();
        $(".floor").each(function(index, element) {
            var floorTop = $(element).offset().top;
            var floorHeight = $(element).height();
            if (scrollTop >= floorTop - 100 && scrollTop < floorTop + floorHeight - 100) {
                $(".left_floor li").removeClass("active");
                $(".left_floor li").eq(index).addClass("active");
            }
        });
    });
});
