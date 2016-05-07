(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .factory('LogsService', LogsService);

    LogsService.$inject = ['$resource'];

    function LogsService ($resource) {
        var service = $resource('api/logs', {}, {
            'findAll': { method: 'GET', isArray: true},
            'changeLevel': { method: 'PUT'}
        });

        return service;
    }
})();
