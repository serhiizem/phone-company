'use strict';

angular.module('phone-company').controller('AuthorizeController', [
    '$scope',
    '$q',
    '$http',
    '$location',
    'SessionService',
    'LoginService',
    'UserService',
    '$rootScope',
    '$routeParams',
    function ($scope, $q, $http, $location, SessionService, LoginService,
              UserService, $rootScope, $routeParams) {
        console.log('This is AuthorizeController');
        $scope.selected = 'signIn';

        if ($routeParams['success'] === 'success') {
            toastr.success('U have successfully completed registration. ' +
                'You can login');
        }

        $scope.user = {
            email: "",
            password: "",
            fistName: "",
            secondName: "",
            lastName: "",
            phone: "",
            address: {
                region: "",
                locality: "",
                street: "",
                houseNumber: "",
                apartmentNumber: ""
            }
        };

        $scope.resetRequest = {
            email: ""
        };

        $scope.registerUser = function () {
            var deferred = $q.defer();
            console.log('Persisting user: ' + JSON.stringify($scope.user));
            $http.post("/api/users", $scope.user).then(
                function (response) {
                    console.log(JSON.stringify(response.data));
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(JSON.stringify(errResponse.data));
                    toastr.error(errResponse.data);
                    deferred.reject(errResponse);
                });
            return deferred.promise;
        };

        $scope.resetPassword = function () {
            console.log('Attempting to reset password for user with email: '
                + JSON.stringify($scope.resetRequest));
            UserService.resetPassword($scope.resetRequest.email)
                .then(function (data) {
                    if (data.msg === 'error') {
                        toastr.error('User with such email was not found!',
                            'Error during restoring password!');
                    } else {
                        console.log("Password was restored for user with email: ", data);
                        $scope.selected = 'signIn';
                        toastr.info('New password has been sent your email!', 'Password was restored!');
                    }
                });
        };

        $scope.loginClick = function () {
            LoginService.login("username=" + $scope.user.email +
                "&password=" + $scope.user.password).then(function (data) {
                    LoginService.tryLogin().then(function (response) {
                        var loggedInRole = '/' + response.replace(/['"]+/g, '');
                        console.log('Currently logged in role is: ' + loggedInRole);
                        var redirectionUrl = loggedInRole.toLowerCase();
                        console.log('Redirecting to: ' + redirectionUrl);
                        $location.path(redirectionUrl);
                    });
                },
                function (data) {
                    toastr.error('Bad credentials', 'Error');
                });
        };

    }]);