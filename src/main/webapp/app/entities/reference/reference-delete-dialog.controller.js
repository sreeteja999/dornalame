(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .controller('ReferenceDeleteController',ReferenceDeleteController);

    ReferenceDeleteController.$inject = ['$uibModalInstance', 'entity', 'Reference'];

    function ReferenceDeleteController($uibModalInstance, entity, Reference) {
        var vm = this;
        vm.reference = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Reference.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
