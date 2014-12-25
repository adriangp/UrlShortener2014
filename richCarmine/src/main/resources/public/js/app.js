angular.module('shortener', ['ui.router'])

    .config(function($stateProvider, $urlRouterProvider){
            $stateProvider

                .state('inicio', {
                    url: "/inicio",
                    templateUrl: "templates/main/inicio.html",
                    controller: "MainCtrl"
                });
            $urlRouterProvider.otherwise('inicio');
        })

    .controller('MainCtrl', [ '$scope', '$http', function($scope,$http){
        //TEST
        $scope.noresult = true;
        $scope.qrcheck = false;
        $scope.qruri = "img/spinner.gif";

        $scope.shortenURL = function(){

            $scope.noresult = true;
            $scope.qruri = "img/spinner.gif";

            if($scope.qrcheck){
                $http({
                    method: 'POST',
                    url: 'http://'+window.location.host+'/qr',
                    data: 'url='+$scope.longurl,
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).success(function(data){
                    $scope.noresult = false;
                    $scope.shorturl = data.uri;
                    $scope.qruri = 'http://'+window.location.host+'/qr'+data.hash;
                })
            }else{
                $http({
                    method: 'POST',
                    url: 'http://'+window.location.host+'/link',
                    data: 'url='+$scope.longurl,
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).success(function(data){
                    $scope.noresult = false;
                    $scope.shorturl = data.uri;
                })
            }
        }

        $scope.qrrize= function(){
            console.log("Changed QR status");
            $scope.qrcheck = ! $scope.qrcheck;
        }

        $scope.CSVtoShortURL = function(){
            console.log($scope.form_csv);
        }
    }])
    
    .directive('footer',function(){
    	return{
    		restrict: 'E',
    		templateUrl: 'templates/components/footer.html'
    	
    	}
    });