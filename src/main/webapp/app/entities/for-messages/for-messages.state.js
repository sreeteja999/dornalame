(function() {
    'use strict';

    angular
        .module('mothersDayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('for-messages', {
            parent: 'entity',
            url: '/for-messages',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mothersDayApp.forMessages.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/for-messages/for-messages.html',
                    controller: 'ForMessagesController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('forMessages');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('for-messages-detail', {
            parent: 'entity',
            url: '/for-messages/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mothersDayApp.forMessages.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/for-messages/for-messages-detail.html',
                    controller: 'ForMessagesDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('forMessages');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ForMessages', function($stateParams, ForMessages) {
                    return ForMessages.get({id : $stateParams.id});
                }]
            }
        })
        .state('for-messages.new', {
            parent: 'for-messages',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/for-messages/for-messages-dialog.html',
                    controller: 'ForMessagesDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                messages: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('for-messages', null, { reload: true });
                }, function() {
                    $state.go('for-messages');
                });
            }]
        })
        .state('for-messages.edit', {
            parent: 'for-messages',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/for-messages/for-messages-dialog.html',
                    controller: 'ForMessagesDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ForMessages', function(ForMessages) {
                            return ForMessages.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('for-messages', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('for-messages.delete', {
            parent: 'for-messages',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/for-messages/for-messages-delete-dialog.html',
                    controller: 'ForMessagesDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ForMessages', function(ForMessages) {
                            return ForMessages.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('for-messages', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
