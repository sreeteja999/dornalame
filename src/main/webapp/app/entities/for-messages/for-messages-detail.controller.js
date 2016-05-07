(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .controller('ForMessagesDetailController', ForMessagesDetailController);

    ForMessagesDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'ForMessages'];

    function ForMessagesDetailController($scope, $rootScope, $stateParams, entity, ForMessages) {
        var vm = this;
        vm.forMessages = entity;
        
        var unsubscribe = $rootScope.$on('mothersDayApp:forMessagesUpdate', function(event, result) {
            vm.forMessages = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
