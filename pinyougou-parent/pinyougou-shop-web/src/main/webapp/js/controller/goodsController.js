//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                editor.html($scope.entity.goodsDesc.introduction);//商品介绍
                //商品图片
           $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
           //扩展属性
            $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);

                //规格选择
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //转换SKU列表中的规格对象
                for(i=0;i< $scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse( $scope.entity.itemList[i].spec);

                }

            }
        );
    }
    //保存
    $scope.save=function(){
        //提取文本编辑器的值
        $scope.entity.goodsDesc.introduction=editor.html();
        var serviceObject;//服务层对象
        if($scope.entity.goods.id!=null){//如果有 ID
            serviceObject=goodsService.update( $scope.entity ); //修改
        }else{
            serviceObject=goodsService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    alert('保存成功');
                    location.href="goods.html";//跳转到商品列表页
                }else{
                    alert(response.message);
                }
            }
        );
    }




    //批量删除
    $scope.dele = function () {
        alert($scope.selectIds);
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    /**
     * 上传图片
     */
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {//如果上传成功，取出 url
                $scope.image_entity.url = response.message;//设置文件地址
            } else {
                alert(response.message);
            }
        }).error(function () {


            alert("上传发生错误");
        });
    };

    $scope.entity = {goods: {}, goodsDesc: {itemImages: []}};//定义页面实体结构
    //添加图片列表
    $scope.add_image_entity = function () {

        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //列表中移除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }


    //查询一级商品分类列表
    $scope.selectItemCat1List = function () {

        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;

            }
        );


    }
    //查询二级商品分类列表
    //$watch监控变量    newValue得到entity.goods.category1Id变量更新后所得到的值
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        //alert(newValue);
        //通过下面这个方法查询 ：根据上级Id查询下级分类列表
        itemCatService.findByParentId(newValue).success(
            function (response) {

                $scope.itemCat2List = response;


            }
        );
    });
    //三级商品分类列表查询
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        //alert(newValue);
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;

            }
        );
    });
    //读取模板ID entity.goods.category3Id变量只要更新，
    // 就执行function(newValue,oldValue)oldValue更改之前的值，newValue更改之后的值

    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        //通过下面这个方法查询 ：根据上级Id查询下级分类列表得到response实体

        itemCatService.findOne(newValue).success(
            function (response) {
                //把response里的typeId  给typeTemplateId
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        );

    });
    //读取模板id后，读取品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
//通过下面这个方法查询 ：根据上级Id查询下级分类列表
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;//模板对象
                //原来是个字符串   把自己JSON.parse一下就转换成json格式了
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                //品牌列表类型转换

                //扩展属性
                if($location.search()['id']==null){
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                }



            }
        );

//读取规格
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        );

    });


    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};

    $scope.updateSpecAttribute = function ($event, name, value) {
        var object = $scope.searchObjectByKey(
            $scope.entity.goodsDesc.specificationItems, 'attributeName', name);
        if (object != null) {
            if ($event.target.checked) {
                object.attributeValue.push(value);
            } else {//取消勾选
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);//移除选

                //如果选项都取消了，将此条记录移除
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {
            $scope.entity.goodsDesc.specificationItems.push(
                {"attributeName": name, "attributeValue": [value]});
        }
    }


    //创建SKU列表
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];//列表初始化
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList,
                items[i].attributeName, items[i].attributeValue);
        }

    }


    //添加列值
    addColumn = function (list, columnName, conlumnValues) {
        var newList = [];//新的集合
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < conlumnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName] = conlumnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }


    //定义商品状态
    $scope.status = ['未审核', '审核通过', '审核未通过', '已关闭'];
    //查询商品分类列表
    $scope.itemCatList = [];//商品分类列表
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        );
    }
//根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue=function(specName,optionName){

        var items= $scope.entity.goodsDesc.specificationItems;
        var object= $scope.searchObjectByKey(items,'attributeName',specName);//根据键找值
        //根据attributeName去找specName等于空的时候就是没有specName值，这样就返回false
        if(object==null){
            return false;
        }else{
    //根据attributeName去找specName不等于空的时候就是有specName值，这样就返回false
            //用indexOf查attributeValue数组中>=0说明有数据
            if(object.attributeValue.indexOf(optionName)>=0){
                return true;
            }else{
                return false;
            }
        }

    }



});

