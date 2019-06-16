app.controller('baseController', function ($scope) {
    //分页控件配置currentPage：当前页。totalItems总记录数。itemsPerPage每页记录数。 perPageOptions分页选项。
    //分页控件配置  onChange当页码变更后自动触发的方法
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();
        }
    };
    //刷新列表
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage);
    }

    $scope.selectIds = [];//要被删除选中的选项的ID
    //用户勾选  复选框
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {//如果被选中
            $scope.selectIds.push(id);//就增加到删除数组
        } else {//如果之前选了又取消了   就删除之前选的  只留下 想要删除的
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除
        }
    }

    $scope.jsonToString=function (jsonToString ,key) {
      var  json=  JSON.parse(jsonToString);
      var    value="";

      for (var i=0;i<json.length;i++){
          if (i>0){
              value +=",";
          }
        value +=json[i][key];
      }


      return value;

    }

    //在list集合中根据某Key的值查询    对象
    $scope.searchObjectByKey=function (list,key,keyValue) {
        for(var i=0;i<list.length;i++){
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;

    }
});