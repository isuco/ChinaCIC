$(document).ready(function(){
    /**
     * 点击投票动画与逻辑
     */
    $(".chose-prize").on("click",function(e){
        // 点击的格子
        let evnetDOM;
        if (e.target.tagName === "IMG"){
            eventDOM = $(e.target).parent();
        }else{
            eventDOM = $(e.target);
        }

        // 获取点击的项目id
        const selectedID = eventDOM.parent().children('#projectId').text();
        // 获取点击的奖项
        const selectedPrize = eventDOM.data("prize");
        // 点击后的状态
        const checked = !eventDOM.data("checked");

        console.log(selectedID, selectedPrize, checked);
        // 把对应项目的input设置成selectedPrize
        $('input[name=' + selectedID + ']').val(checked?selectedPrize:"无");
        // 把整行其他checked状态全部设为false
        // 并把所有的selected img 设置成 selected
        eventDOM.parent().children(".chose-prize").data("checked", false);
        eventDOM.parent().children(".chose-prize").children("img").attr("src", "/img/unSelected.png");

        // 把自己设置成 checked
        eventDOM.data("checked", checked);
        if (checked){
            eventDOM.children("img").attr("src", "/img/selected.png");
        }
    });
});
