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

    $(".sort a").click(function() {
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
        var sort = $(this).attr("data-sort");
        var url = window.location.href;
        if (url.indexOf("sort=") > -1) {
            url = url.replace(/sort=[^&]*/, "sort=" + sort);
        } else {
            if (url.indexOf("?") > -1) {
                url += "&sort=" + sort;
            } else {
                url += "?sort=" + sort;
            }
        }
        window.location.href = url;
    });

    $(".pagination a").click(function() {
        var page = $(this).attr("data-page");
        var url = window.location.href;
        if (url.indexOf("page=") > -1) {
            url = url.replace(/page=[^&]*/, "page=" + page);
        } else {
            if (url.indexOf("?") > -1) {
                url += "&page=" + page;
            } else {
                url += "?page=" + page;
            }
        }
        window.location.href = url;
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
