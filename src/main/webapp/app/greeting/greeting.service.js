(function() {
    'use strict';
    angular
        .module('mothersDayApp')
        .factory('GreetingMessages', GreetingMessages);

    GreetingMessages.$inject = ['$resource'];

    function GreetingMessages ($resource) {
        var resourceUrl =  'public/for-messages/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
