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

    .controller('MassiveCtrl', ['$scope', '$http', function ($scope, $http) {
        $scope.max = 0;
        $scope.lines = [];
        $scope.hideData = true;
        $scope.loaded = 0;
        $scope.loaded_per = 0;
        $scope.urls = []
        $scope.csv_uri = "";

        var ws_uri = "ws://" + window.location.host + "/ws/naivews";

        var ws = new WebSocket(ws_uri);

        ws.onmessage = function(event) {
            data = JSON.parse(event.data);
            if(data.hasOwnProperty("csv")){
                $scope.$apply(function(){
                    $scope.csv_uri = "http://" + window.location.host + "/" + data.uri;
                });
                console.log(data);
            }
            else{
                $scope.$apply(function(){
                    $scope.loaded ++;
                    $scope.loaded_per = $scope.loaded/$scope.max * 100;
                    $scope.urls.push(data);
                });
            }
        };
        $scope.showContent = function($fileContent){
            $scope.content = $fileContent;
        };
        $scope.uploadContent = function(){
            $scope.hideData = false;
            $scope.lines = $scope.content.split("\n");
            $scope.max = $scope.lines.length;

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
        };
        $scope.downloadCSV = function(){
//            window.open($scope.csv_uri,"_blank");
            window.location.assign($scope.csv_uri);
        };
    }])

    // http://uncorkedstudios.com/blog/multipartformdata-file-upload-with-angularjs
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

    .directive('footer', function () {
        return{
            restrict: 'E',
            templateUrl: 'templates/components/footer.html'

        }
    });