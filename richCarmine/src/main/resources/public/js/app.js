angular.module('shortener', ['ui.router','ui.bootstrap'])

    .config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider

            .state('inicio', {
                url: "/inicio",
                templateUrl: "templates/main/inicio.html",
                controller: "MainCtrl"
            })
            .state('csv', {
                url: "/massive",
                templateUrl: "templates/main/massive.html",
                controller: "MassiveCtrl"
            })
            .state('restcsv', {
                url: "/rest/massive",
                templateUrl: "templates/main/restMassive.html",
                controller: "RestMassiveCtrl"
            });
        $urlRouterProvider.otherwise('inicio');
    })


    .service('fileUpload', ['$http', function ($http) {
        this.uploadFileToUrl = function (file, uploadUrl) {
            console.log("ON SERVICE");
            var fd = new FormData();
            fd.append('file', file);
            $http.post(uploadUrl, fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': "multipart/form-data"}
            })
                .success(function (data) {
                    console.log(data);
                })
                .error(function () {
                });
        }
    }])

    .controller('MainCtrl', [ '$scope', '$http', 'fileUpload', function ($scope, $http, fileUpload) {
        //TEST
        $scope.noresult = true;
        $scope.qrcheck = false;
        $scope.qruri = "img/spinner.gif";

        $scope.shortenURL = function () {

            $scope.noresult = true;
            $scope.qruri = "img/spinner.gif";

            if ($scope.qrcheck) {
                $http({
                    method: 'POST',
                    url: 'http://' + window.location.host + '/qr',
                    data: 'url=' + $scope.longurl,
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).success(function (data) {
                    $scope.noresult = false;
                    $scope.shorturl = data.uri;
                    $scope.qruri = 'http://' + window.location.host + '/qr' + data.hash;
                })
            } else {
                $http({
                    method: 'POST',
                    url: 'http://' + window.location.host + '/link',
                    data: 'url=' + $scope.longurl,
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).success(function (data) {
                    $scope.noresult = false;
                    $scope.shorturl = data.uri;
                })
            }
        }

        $scope.qrrize = function () {
            console.log("Changed QR status");
            $scope.qrcheck = !$scope.qrcheck;
        }

        $scope.CSVtoShortURL = function () {
            console.log($scope.form_csv);
        }

        $scope.uploadFile = function () {
            console.log("upload FILE");
//            $http.post("http://" + window.location.host + "/rest/csv", fd,{
            var file = $scope.myFile;
            console.log('file is ' + JSON.stringify(file));
            var uploadUrl = "http://" + window.location.host + "/rest/csv";
            fileUpload.uploadFileToUrl(file, uploadUrl);
        }
    }])

    .controller('MassiveCtrl', ['$scope', function ($scope) {
        $scope.max = 0;
        $scope.lines = [];
        $scope.hideData = true;
        $scope.loaded = 0;
        $scope.loaded_per = 0;
        $scope.urls = []
        $scope.csv_uri = "";
        var data_to_show = 50;

        var ws_uri = "ws://" + window.location.host + "/ws/naivews";
        $scope.showContent = function($fileContent){
            $scope.content = $fileContent;
        };
        $scope.uploadContent = function(){
            var ws = new WebSocket(ws_uri);

            ws.onmessage = function(event) {
                data = JSON.parse(event.data);
                if(data.hasOwnProperty("csv")){
                    $scope.$apply(function(){
                        $scope.csv_uri = "http://" + window.location.host + "/" + data.uri;
                    });
                    if($scope.max > data_to_show){
                        $scope.urls = data.csv;
                    }
                }
                else{
                    $scope.$apply(function(){
                        $scope.loaded ++;
                        $scope.loaded_per = $scope.loaded/$scope.max * 100;
                        if($scope.max <= data_to_show){
                            $scope.urls.push(data);
                        }
                    });
                }
            };


            $scope.max = 0;
            $scope.loaded = 0;
            $scope.loaded_per = 0;
            $scope.urls = [];
            $scope.hideData = false;
            $scope.lines = $scope.content.split("\n");
            $scope.max = $scope.lines.length;

            ws.onopen = function() {
                $scope.lines.forEach(function(e) {
                    if(e !== ""){
                        ws.send(e);
                    }
                    else{
                        $scope.max --;
                        $scope.loaded_per = $scope.loaded/$scope.max * 100;
                    }
                });
                ws.send("<<EOF>>");
            }
        };
        $scope.downloadCSV = function(){
            window.location.assign($scope.csv_uri);
        };
    }])

    .directive('onReadFile', ['$parse', function ($parse) {
        return {
            restrict: 'A',
            scope: false,
            link: function($scope, element, attrs) {
                var fn = $parse(attrs.onReadFile);

                element.on('change', function(onChangeEvent) {
                    var reader = new FileReader();

                    reader.onload = function(onLoadEvent) {
                        $scope.$apply(function() {
                            fn($scope, {$fileContent:onLoadEvent.target.result});
                        });
                    };

                    reader.readAsText((onChangeEvent.srcElement || onChangeEvent.target).files[0]);
                });
            }
        };
    }])

//    REST CONTROLLER AND STUFF
    .directive('fileModel', ['$parse', function ($parse) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var model = $parse(attrs.fileModel);
                var modelSetter = model.assign;

                element.bind('change', function(){
                    scope.$apply(function(){
                        modelSetter(scope, element[0].files[0]);
                    });
                });
            }
        };
    }])
    .service('fileUpload', ['$http', '$rootScope', function ($http, $rootScope) {
        $rootScope.btn_hide = true;
        $rootScope.urls = [];
        this.uploadFileToUrl = function(file, uploadUrl){
            var fd = new FormData();
            fd.append('file', file);
            $http.post(uploadUrl, fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
                .success(function(data){
                    console.log("GOT SMTH!");
                    console.log(data);
                    $rootScope.btn_hide = false;
                    $rootScope.csv_uri = data.uri;
                    $rootScope.urls = data.csv;
                })
                .error(function(){
                });
        }
        $rootScope.downloadCSV = function(){
            window.location.assign($rootScope.csv_uri);
//            console.log($rootScope.csv_uri);
//            console.log($rootScope.btn_hide);
//            console.log($rootScope.urls);
        };
        return this;
    }])
    .controller('RestMassiveCtrl', ['$scope', 'fileUpload', function($scope, fileUpload){

        $scope.uploadFile = function(){
            var file = $scope.myFile;
            var uploadUrl = "/rest/csv";
            fileUpload.uploadFileToUrl(file, uploadUrl);
        };
    }])

    .directive('footer', function () {
        return{
            restrict: 'E',
            templateUrl: 'templates/components/footer.html'

        }
    });