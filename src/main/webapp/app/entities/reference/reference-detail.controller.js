(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .controller('ReferenceDetailController', ReferenceDetailController);

    ReferenceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Reference'];

    function ReferenceDetailController($scope, $rootScope, $stateParams, entity, Reference) {
        var vm = this;
        vm.reference = entity;
        
        var unsubscribe = $rootScope.$on('mothersDayApp:referenceUpdate', function(event, result) {
            vm.reference = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
