(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .controller('ReferenceDialogController', ReferenceDialogController);

    ReferenceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Reference'];

    function ReferenceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Reference) {
        var vm = this;
        vm.reference = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('mothersDayApp:referenceUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.reference.id !== null) {
                Reference.update(vm.reference, onSaveSuccess, onSaveError);
            } else {
                Reference.save(vm.reference, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
