app.controller('brandController', function ($scope, $controller, brandService) {

    $controller('baseController', {$scope:$scope});


    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }


    //分页
    $scope.findPage = function (page, size) {

        brandService.findPage(page, size).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //新增
    $scope.save = function () {
        var object = null;//定义对象
        if ($scope.entity.id != null) {//如果查询的指定打得ID不等于空的话那么就调用  修改的方法
            object = brandService.update($scope.entity);
        } else {
            object = brandService.add($scope.entity);
        }

        object.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新
                } else {
                    alert(response.message);
                }
            }
        );
    }
    //查询实体
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //删除


    //批量删除
    $scope.dele = function () {
        brandService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {//如果删除成功就 刷新列表
                    $scope.reloadList();
                } else {               //如果删除失败就   显示message
                    alert(response.message);
                }
            }
        );

    }

    $scope.searchEntity = {};//初始化 一个空的对象
    //条件查询
    $scope.search = function (page, size) {


        brandService.search(page, size, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );

    }


});
