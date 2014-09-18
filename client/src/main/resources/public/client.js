var appName = 'client';

/*******************************************************************************
 * Uploads images to made doge-tastic
 */
require.config({
    paths: {
        doge: 'doge',
        stomp: doge.jsUrl('stomp-websocket/lib/stomp'),
        sockjs: doge.jsUrl('sockjs/sockjs'),
        angular: doge.jsUrl('angular/angular'),
        angularFileUpload: doge.jsUrl('ng-file-upload/angular-file-upload'),
        domReady: doge.jsUrl('requirejs-domready/domReady')
    },
    shim: {
        angular: {
            exports: 'angular'
        }
    }
});

define([ 'require', 'angular' ], function (require, angular) {

    'use strict';

    require([ 'angular', 'angularFileUpload', 'sockjs', 'stomp', 'domReady!' ],
        function (angular) {
            angular.bootstrap(document, [ appName ]);
        });

    angular.module(appName, [ 'angularFileUpload' ]).controller('ClientController', [ '$scope', '$http', '$upload', '$log', function ($scope, $http, $upload, $log) {

        $scope.uploads = [];
        $scope.users = [];
        $scope.selectedUser = null;

        $http.get('/api/accounts').success(function (data) {

            $scope.users = data['_embedded']['accounts'];

            if ($scope.users != null && $scope.users.length > 0) {

                $scope.selectedUser = $scope.users[0];
            }
        });

        $scope.uploadJson = '';

        $scope.onFileSelect = function ($files) {
            for (var i = 0; i < $files.length; i++) {

                console.log('attempting to upload ' + $files[i] + '');

                var baseDogeUrl = '/api/doges/';

                var key = $scope.selectedUser.id;
                $scope.upload = $upload.upload({
                    url: baseDogeUrl + key + '/photos',
                    method: 'POST',
                    file: $files[i],
                    fileFormDataName: 'file'
                }).success(function (data, status, headers, config) {

                    var imgUrl = data['dogePhotoUri'];

                    $scope.uploads.push(imgUrl);

                    console.log('length=' + $scope.uploads.length + ' : ' + imgUrl);


                    console.log('just added ' + JSON.stringify($scope.uploads))
                    $scope.uploadJson = JSON.stringify($scope.uploads);
                });


            }
        };


    }]);

});

/*
 'use strict';

 require([ 'angular', 'angularFileUpload', 'sockjs', 'stomp', 'domReady!' ],
 function (angular) {
 angular.bootstrap(document, [ appName ]);
 });

 angular.module(appName, [ 'angularFileUpload' ]).controller('ClientController',
 [ '$scope', '$http', '$upload', '$log', function ($scope, $http, $upload, $log) {

 $scope.users = [];
 $scope.dogeUploads = [];

 $http.get('/api/accounts').success(function (data) {
 $scope.users = data;
 if ($scope.users != null && $scope.users.length > 0) {
 $scope.selectedUser = $scope.users[0];
 }
 $scope.users.every(function (u) {
 console.log('user ' + u)
 })
 });

 $scope.onFileSelect = function($files) {
 for (var i = 0; i < $files.length; i++) {
 $scope.upload = $upload.upload({
 url : '/api/doges/' + $scope.selectedUser.id + '/photos',
 method : 'POST',
 file : $files[i],
 fileFormDataName: 'file'
 }).success(function(data, status, headers, config) {
 $scope.dogeUploads.splice(0, 0, headers('location'));
 });
 }
 }

 } ]); */
