'use strict';

describe('Controller Tests', function() {

    describe('ForMessages Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockForMessages;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockForMessages = jasmine.createSpy('MockForMessages');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ForMessages': MockForMessages
            };
            createController = function() {
                $injector.get('$controller')("ForMessagesDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'mothersDayApp:forMessagesUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
