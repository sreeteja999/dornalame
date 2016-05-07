(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('reference', {
            parent: 'entity',
            url: '/reference?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mothersDayApp.reference.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/reference/references.html',
                    controller: 'ReferenceController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('reference');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('reference-detail', {
            parent: 'entity',
            url: '/reference/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mothersDayApp.reference.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/reference/reference-detail.html',
                    controller: 'ReferenceDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('reference');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Reference', function($stateParams, Reference) {
                    return Reference.get({id : $stateParams.id});
                }]
            }
        })
        .state('reference.new', {
            parent: 'reference',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/reference/reference-dialog.html',
                    controller: 'ReferenceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                ref_name: null,
                                ref_url: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('reference', null, { reload: true });
                }, function() {
                    $state.go('reference');
                });
            }]
        })
        .state('reference.edit', {
            parent: 'reference',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/reference/reference-dialog.html',
                    controller: 'ReferenceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Reference', function(Reference) {
                            return Reference.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('reference', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('reference.delete', {
            parent: 'reference',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/reference/reference-delete-dialog.html',
                    controller: 'ReferenceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Reference', function(Reference) {
                            return Reference.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('reference', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
