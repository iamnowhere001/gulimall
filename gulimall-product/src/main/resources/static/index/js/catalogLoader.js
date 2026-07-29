$(function() {
    // 加载二级、三级分类导航数据
    // 接口返回 Map<String, List<Catelog2Vo>>，key 为一级分类 id
    var catalogData = null;

    $.getJSON("/index/catalog.json", function(data) {
        catalogData = data;
    }).fail(function() {
        console.log("分类数据加载失败");
    });

    // 鼠标悬停一级分类时，展示二级、三级分类
    $(".header_main_left_a").mouseenter(function() {
        var cat1Id = $(this).attr("ctg-data");
        if (!catalogData || !cat1Id) {
            return;
        }

        var cat2List = catalogData[cat1Id];
        if (!cat2List || cat2List.length === 0) {
            return;
        }

        // 移除之前可能存在的弹出层
        $(this).siblings(".header_main_left_cat2").remove();

        var $popup = $("<div class='header_main_left_cat2'></div>");

        cat2List.forEach(function(cat2) {
            var $cat2Item = $("<div class='cat2_item'></div>");
            $cat2Item.append($("<a class='cat2_name'></a>").attr("href", "#").text(cat2.name));

            if (cat2.catalog3List && cat2.catalog3List.length > 0) {
                var $cat3Box = $("<div class='cat3_box'></div>");
                cat2.catalog3List.forEach(function(cat3) {
                    $cat3Box.append($("<a class='cat3_name'></a>").attr("href", "#").text(cat3.name));
                });
                $cat2Item.append($cat3Box);
            }
            $popup.append($cat2Item);
        });

        $(this).after($popup);
    });

    // 鼠标离开一级分类项时，移除弹出层
    $(".header_main_left li").mouseleave(function() {
        $(this).find(".header_main_left_cat2").remove();
    });
});
