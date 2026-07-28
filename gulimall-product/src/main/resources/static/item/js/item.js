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

    $(".main_left_bottom ul li").click(function() {
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
        var src = $(this).find("img").attr("src");
        $(".main_left_top img").attr("src", src);
    });

    $(".main_left_top").mouseenter(function() {
        $(this).find(".zoom").css("display", "block");
    });

    $(".main_left_top").mouseleave(function() {
        $(this).find(".zoom").css("display", "none");
    });

    $(".main_left_top").mousemove(function(event) {
        var offset = $(this).offset();
        var x = event.pageX - offset.left - 50;
        var y = event.pageY - offset.top - 50;
        if (x < 0) x = 0;
        if (x > 250) x = 250;
        if (y < 0) y = 0;
        if (y > 250) y = 250;
        $(this).find(".zoom").css({left: x, top: y});
    });

    $(".sku_value").click(function() {
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
    });

    $(".quantity_box button").click(function() {
        var input = $(this).siblings("input");
        var value = parseInt(input.val());
        if ($(this).html() === "-") {
            if (value > 1) {
                input.val(value - 1);
            }
        } else {
            input.val(value + 1);
        }
    });

    $(".tabs ul li").click(function() {
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
        var index = $(this).index();
        $(".tab_content").removeClass("active");
        $(".tab_content").eq(index).addClass("active");
    });

    $(".add_cart").click(function() {
        var skuId = $("#skuId").val();
        var num = $(".quantity_box input").val();
        var url = "http://cart.gulimall.com/addToCart?skuId=" + skuId + "&num=" + num;
        $.get(url, function(data) {
            if (data.code === 0) {
                alert("添加购物车成功");
                window.location.href = "http://cart.gulimall.com/cart.html";
            } else {
                alert(data.message);
            }
        });
    });

    $(".buy_now").click(function() {
        var skuId = $("#skuId").val();
        var num = $(".quantity_box input").val();
        var url = "http://cart.gulimall.com/addToCart?skuId=" + skuId + "&num=" + num;
        $.get(url, function(data) {
            if (data.code === 0) {
                window.location.href = "http://cart.gulimall.com/cart.html";
            } else {
                alert(data.message);
            }
        });
    });

    $(".header_form input").keydown(function(event) {
        if (event.keyCode === 13) {
            var keyword = $(this).val();
            if (keyword) {
                window.location.href = "http://search.gulimall.com/list.html?keyword=" + encodeURIComponent(keyword);
            }
        }
    });

    $(".header_form a").click(function() {
        var keyword = $(".header_form input").val();
        if (keyword) {
            window.location.href = "http://search.gulimall.com/list.html?keyword=" + encodeURIComponent(keyword);
        }
    });
});
