(function() {
    'use strict';
    angular
        .module('mothersDayApp')
        .factory('ForMessages', ForMessages);

    ForMessages.$inject = ['$resource'];

    function ForMessages ($resource) {
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
