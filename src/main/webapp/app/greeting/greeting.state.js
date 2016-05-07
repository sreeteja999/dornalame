(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('greeting', {
            parent: 'app',
            url: '/greeting',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/greeting/greeting.html',
                    controller: 'GreetingsController',
                    controllerAs: 'gm'
                }
            },
            resolve: {
                mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                    $translatePartialLoader.addPart('greetings');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
