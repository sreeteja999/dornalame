(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .factory('ReferenceSearch', ReferenceSearch);

    ReferenceSearch.$inject = ['$resource'];

    function ReferenceSearch($resource) {
        var resourceUrl =  'api/_search/references/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
