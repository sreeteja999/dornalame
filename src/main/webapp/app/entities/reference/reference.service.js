(function() {
    'use strict';
    angular
        .module('mothersDayApp')
        .factory('Reference', Reference);

    Reference.$inject = ['$resource'];

    function Reference ($resource) {
        var resourceUrl =  'public/references/:id';

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
