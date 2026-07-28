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

    $(".quantity_box button").click(function() {
        var input = $(this).siblings("input");
        var value = parseInt(input.val());
        var skuId = $(this).closest("tr").find(".cart_check input").val();
        if ($(this).html() === "-") {
            if (value > 1) {
                input.val(value - 1);
                updateCart(skuId, value - 1);
            }
        } else {
            input.val(value + 1);
            updateCart(skuId, value + 1);
        }
    });

    function updateCart(skuId, num) {
        $.get("http://cart.gulimall.com/updateCartItem?skuId=" + skuId + "&num=" + num, function(data) {
            if (data.code === 0) {
                window.location.reload();
            } else {
                alert(data.message);
            }
        });
    }

    $(".cart_check input").click(function() {
        var checked = $(this).prop("checked");
        if (checked) {
            $(this).closest("tr").addClass("checked");
        } else {
            $(this).closest("tr").removeClass("checked");
        }
        updateTotal();
    });

    $(".cart_bottom_left input").click(function() {
        var checked = $(this).prop("checked");
        $(".cart_check input").prop("checked", checked);
        if (checked) {
            $(".cart_table table tbody tr").addClass("checked");
        } else {
            $(".cart_table table tbody tr").removeClass("checked");
        }
        updateTotal();
    });

    function updateTotal() {
        var total = 0;
        var count = 0;
        $(".cart_check input:checked").each(function() {
            var row = $(this).closest("tr");
            var price = parseFloat(row.find(".cart_price span").text());
            var num = parseInt(row.find(".quantity_box input").val());
            total += price * num;
            count += num;
        });
        $(".cart_bottom_right .total_price span").text(total.toFixed(2));
        $(".header_gw span:last-child").text(count);
    }

    $(".cart_action a").click(function() {
        var skuId = $(this).closest("tr").find(".cart_check input").val();
        if (confirm("确定删除该商品吗？")) {
            $.get("http://cart.gulimall.com/deleteCartItem?skuId=" + skuId, function(data) {
                if (data.code === 0) {
                    window.location.reload();
                } else {
                    alert(data.message);
                }
            });
        }
    });

    $(".cart_bottom_left a").click(function() {
        if (confirm("确定清空购物车吗？")) {
            $.get("http://cart.gulimall.com/clearCart", function(data) {
                if (data.code === 0) {
                    window.location.reload();
                } else {
                    alert(data.message);
                }
            });
        }
    });

    $(".cart_bottom_right button").click(function() {
        var checkedCount = $(".cart_check input:checked").length;
        if (checkedCount === 0) {
            alert("请选择要结算的商品");
            return;
        }
        window.location.href = "http://order.gulimall.com/order.html";
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
