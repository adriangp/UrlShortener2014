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
        $scope.qruri = undefined;

        $scope.shortenURL = function(){

            $scope.noresult = true;
            $scope.qruri = undefined;
            console.log($scope.qrcheck);
            $http({
                method: 'POST',
                url: 'http://10.3.14.76:8080/qr',
                data: 'url='+$scope.longurl,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }

            }).success(function(data){
                $scope.noresult = false;
                if($scope.qrcheck){
                    $scope.shorturl = data.uri;

                    $scope.qruri = 'http://10.3.14.76:8080/qr'+data.hash;
                }else{
                    $scope.shorturl = data.uri;
                }
            })

        }

        $scope.qrrize= function(){
            console.log("Changed QR status");
            $scope.qrcheck = ! $scope.qrcheck;
        }

        $scope.ShortURLtoQR = function(){

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