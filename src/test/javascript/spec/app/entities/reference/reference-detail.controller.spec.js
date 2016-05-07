'use strict';

describe('Controller Tests', function() {

    describe('Reference Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockReference;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockReference = jasmine.createSpy('MockReference');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Reference': MockReference
            };
            createController = function() {
                $injector.get('$controller')("ReferenceDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'mothersDayApp:referenceUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
