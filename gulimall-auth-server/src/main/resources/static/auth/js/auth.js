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

    $(".captcha_img").click(function() {
        $(this).attr("src", $(this).attr("src") + "&t=" + Math.random());
    });

    $(".auth_tabs ul li").click(function() {
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
        var index = $(this).index();
        if (index === 0) {
            window.location.href = "http://auth.gulimall.com/login.html";
        } else {
            window.location.href = "http://auth.gulimall.com/reg.html";
        }
    });

    $(".auth_btn").click(function() {
        var formData = {};
        $(this).closest(".auth_form").find("input").each(function() {
            formData[$(this).attr("name")] = $(this).val();
        });
        var url = $(this).closest(".auth_container").hasClass("login_container") 
            ? "http://auth.gulimall.com/login" 
            : "http://auth.gulimall.com/register";
        $.post(url, formData, function(data) {
            if (data.code === 0) {
                alert("操作成功");
                window.location.href = "http://gulimall.com";
            } else {
                alert(data.message);
            }
        });
    });

    $(".agreement").click(function() {
        if (!$(this).prop("checked")) {
            alert("请同意用户协议");
            $(this).prop("checked", true);
        }
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
