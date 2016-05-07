(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .controller('ForMessagesDeleteController',ForMessagesDeleteController);

    ForMessagesDeleteController.$inject = ['$uibModalInstance', 'entity', 'ForMessages'];

    function ForMessagesDeleteController($uibModalInstance, entity, ForMessages) {
        var vm = this;
        vm.forMessages = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            ForMessages.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
