app.controller('contentController',function ($scope,contentService) {
  //因为广告列表很多 所以 定义一个空的  类似于Map集合
    $scope.contentList=[];//所有的广告列表 以下标 的形式存取
    //根据广告分类ID查询广告分类列表
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId]=response;
            }
        );
    }

    //搜索跳转
    $scope.search=function(){
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
    
});