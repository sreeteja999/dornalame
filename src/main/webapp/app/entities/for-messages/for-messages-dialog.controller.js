(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .controller('ForMessagesDialogController', ForMessagesDialogController);

    ForMessagesDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ForMessages'];

    function ForMessagesDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ForMessages) {
        var vm = this;
        vm.forMessages = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('mothersDayApp:forMessagesUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.forMessages.id !== null) {
                ForMessages.update(vm.forMessages, onSaveSuccess, onSaveError);
            } else {
                ForMessages.save(vm.forMessages, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
