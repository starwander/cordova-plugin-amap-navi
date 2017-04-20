# cordova_plugin_amap_navi
使用[高德地图](http://lbs.amap.com/)sdk进行定位和导航的cordova plugin

使用[科大讯飞](http://www.xfyun.cn/)sdk实现导航语音的合成和播报


目前是实现了Android插件，后续会逐渐添加iOS和其它平台

## 安装插件
androidamapkey: 高德地图android api key

iflytekappid: 科大讯飞app id

```shell
cordova plugin add https://github.com/starwander/cordova-plugin-amap-navi --variable androidamapkey=123 --variable iflytekappid=456
```

## 使用插件
### 定位：
```js
var successCallback = function(data){
	//data.longitude 经度
	//data.latitude 纬度
	//data.accuracy 返回定位精度半径
	//data.address 返回地址的详细描述，包括省、市、区和街道
	//data.floor 返回定位到的室内地图的楼层，如果不在室内或者无数据，则返回默认值null
	//data.province 返回定位位置的提供者名称
	//data.road 返回定位信息中道路名称
	//data.speed 返回定位速度，单位：米/秒，如果此位置不具有速度，则返回0.0
	//data.time 返回定位时间，毫秒时间（距离1970年 1月 1日 00:00:00 GMT的时间）
	//data.hasAccuracy 获取定位精度状态
};

var errorCallback = function(message){
    console.log(message);  
};

AMapNavi.getLocation(successCallback, errorCallback);

```

### 导航：
```js
var successCallback = function(message){
  //do something  
};

var errorCallback = function(message){
    console.log(message);  
};

cordova.plugins.AMapNavigation.navigation({
   lng: 起始地的经度,
   lat: 起始地的纬度
}, {
    lng: 终点的经度,
    lat: 终点的纬度
}, NaviType //导航类型，0为实时，1为模拟
successCallback, errorCallback);

```

## 删除插件

```
cordova plugin remove cordova-plugin-amap-navi
