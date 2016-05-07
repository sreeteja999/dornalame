(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .factory('ForMessagesSearch', ForMessagesSearch);

    ForMessagesSearch.$inject = ['$resource'];

    function ForMessagesSearch($resource) {
        var resourceUrl =  'api/_search/for-messages/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
