(function() {
    'use strict';
    angular
        .module('mothersDayApp')
        .controller('GreetingsController', GreetingsController);

    GreetingsController.$inject = ['$scope', 'Principal','GreetingMessages', '$state', '$timeout'];

    function GreetingsController ($scope, Principal,GreetingMessages, $state, $timeout) {
        var gm = this;
        gm.greetings = [];
        gm.loadAll = function(){
            GreetingMessages.query(function (result) {
                gm.greetings = result;
            });
        };
        gm.loadAll();

        gm.init = function () {
           var elem = angular.element(document.querySelector('#msg1'));
            elem.alert('animated shake');
        }
    }

})();
