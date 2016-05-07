(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .controller('ForMessagesController', ForMessagesController);

    ForMessagesController.$inject = ['$scope', '$state', 'ForMessages', 'ForMessagesSearch'];

    function ForMessagesController ($scope, $state, ForMessages, ForMessagesSearch) {
        var vm = this;
        vm.forMessages = [];
        vm.loadAll = function() {
            ForMessages.query(function(result) {
                vm.forMessages = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ForMessagesSearch.query({query: vm.searchQuery}, function(result) {
                vm.forMessages = result;
            });
        };
        vm.loadAll();
        
    }
})();
