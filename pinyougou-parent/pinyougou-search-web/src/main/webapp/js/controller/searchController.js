app.controller('searchController', function ($scope,$location, searchService) {
    //定义搜索对象的结构  category:商品分类
    $scope.searchMap = {//'sortField将域排序的意思  sort传输的是升序降序
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 40,
        'sort': '',
        'sortField': ''
    };
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                //构建分页栏
                buildPageLabel();

            }
        );
    }
    //构建分页栏
    buildPageLabel = function () {

        $scope.pageLabel = [];
        var firstPage = 1;//开始页码
        var lastPage = $scope.resultMap.totalPages;//截止页

        $scope.firstDot = true;//前面有省略号
        $scope.lastDot = true;//后面有省略号

        if ($scope.resultMap.totalPages > 5) {   //如果页 数大于5的话  就执行以下方法
            if ($scope.searchMap.pageNo <= 3) {//当前页小于等于3  就显示前5页
                lastPage = 5;
                $scope.firstDot = false;//前面没省略号
            } else if ($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2) {//显示后5页
                firstPage = $scope.resultMap.totalPages - 4;
                $scope.lastDot = false;//后面没省略号
            } else {
                //当前页码减2                                            //显示中间页
                firstPage = $scope.searchMap.pageNo - 2;
                //当前页码加2
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            $scope.firstDot = false;//前面没省略号
            $scope.lastDot = false;//后面没省略号
        }


        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }


    //添加搜索项   其实就是给searchMap赋值
    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//查询
    }
    //撤销搜索项
    $scope.removeSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();//查询
    }


    //分页查询
    $scope.queryByPage = function (pageNo) {

        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;//查询点到的当前页
        $scope.search();//查询
    }


    //判断当前页是否为第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    }
    //判断当前页是否为最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        } else {
            return false;
        }
    }

    //排序查询
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;

        $scope.search();//查询

    }

    //判断关键字是否是品牌
    $scope.keywordsIsBrand = function () {
        //循环品牌集合
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            //$scope.searchMap.keywords得到页面输入的信息
             //$scope.resultMap.brandList[i].text得到的是品牌名称
            //$scope.searchMap.keywords.indexOf()得到的是页面上输入信息的位置
             //这句总意思：比如你在页面搜索  三星哈哈三星  有两个品牌   那就 true
            //若   输入 哈哈哈哈  说明信息里面没有 品牌 那么 索引肯定小于0  那么就 false
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0) {
                return true;

            }
        }
        return false;


    }
//加载查询字符串
    $scope.loadkeywords=function(){
        //得到  关键字
        $scope.searchMap.keywords=  $location.search()['keywords'];
        $scope.search();//查询
    }

});