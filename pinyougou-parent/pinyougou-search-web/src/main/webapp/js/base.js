var app = angular.module('pinyougou', []);//定义品优购模块
/*$sce 服务写成过滤器*/
app.filter('trustHtml',['$sce',function($sce){
    return function(data){//传入参数是被过滤的内容
        return $sce.trustAsHtml(data);
    }
}]);