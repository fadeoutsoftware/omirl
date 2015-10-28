/**
 * Created by s.adamo on 27/10/2015.
 */
var UsersController = (function() {
    function UsersController($scope, $location, oConstantsService, oAuthService, oUserService) {
        this.m_oScope = $scope;
        this.m_oLocation  = $location;
        this.m_oConstantsService = oConstantsService;
        this.m_oAuthService = oAuthService;
        this.m_oScope.m_oController = this;
        this.m_oUserService = oUserService;
        var oController = this;

        if (oConstantsService.isUserLogged())
        {
            if (oConstantsService.getUser() != null)
            {
                // only administrator can manage users
                if (oConstantsService.isUserAdministrator() == 1)
                {
                    //Load users
                    oUserService.loadUsers();
                }
            }
        }

        $scope.$on('$locationChangeStart', function (event, next, current) {

            if (oController.m_oUserService.isModified()) {
                var bAnswer = confirm("Are you sure you want to leave this page without saving?");
                if (!bAnswer) {
                    event.preventDefault();
                }
            }
        });


    }

    UsersController.prototype.getUsers = function() {
        return this.m_oUserService.getUsers();

    };

    UsersController.prototype.saveUser = function() {

        var oController = this;
        var oUsers =  oController.m_oUserService.getUsers();
        var lastUser = oUsers[oUsers.length - 1];
        if (lastUser != null)
        {
            if (lastUser.name != null && lastUser.name != '' &&
                lastUser.password != null && lastUser.password != '')
            {
                //save last user
                oController.m_oUserService.saveUsers(lastUser).success(function(data){

                    //load user
                    oController.m_oUserService.loadUsers();
                    oController.getUsers();
                });
            }
        }

    };

    UsersController.prototype.addUser = function() {

        var oController = this;
        var oUsers =  oController.m_oUserService.getUsers();

        if (oUsers.length == 0 || !oController.m_oUserService.isModified())
        {
            oController.m_oUserService.setAsModified();

            //add new user
            var oUser = {
                name: '',
                password: '',
                role: 2,
                userId: ''
            };

            //add to user list
            oUsers.push(oUser);
        }
        else {
            //take last user
            var lastUser = oUsers[oUsers.length - 1];
            if (lastUser != null) {
                if (lastUser.name != null && lastUser.name != '' &&
                    lastUser.password != null && lastUser.password != '') {
                    //save last user
                    oController.m_oUserService.saveUsers(lastUser).success(function (data) {

                        //load user
                        oController.m_oUserService.loadUsers();
                        oController.getUsers();

                        oController.m_oUserService.setAsModified();
                        //add new user
                        var oUser = {
                            name: '',
                            password: '',
                            role: 2,
                            userId: ''
                        };

                        //add to user list
                        oUsers.push(oUser);

                    });

                }
            }
        }

    };

    UsersController.prototype.deleteUser = function(id) {

        var oConroller = this;
        //take last user

        //save last user
        oConroller.m_oUserService.deleteUsers(id).success(function(data){

            //load user
            oConroller.m_oUserService.loadUsers();
        });

    };

    UsersController.$inject = [
        '$scope',
        '$location',
        'ConstantsService',
        'AuthService',
        'UserService'
    ];

    return UsersController;
}) ();
