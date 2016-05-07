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
    }

    function init(){
        $timeout(function () {
        },2000);
    }


})();
