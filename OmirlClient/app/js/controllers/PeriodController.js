/**
 * Created by s.adamo on 11/04/2016.
 */

/**
 * Created by s.adamo on 27/10/2015.
 */
var PeriodController = (function() {
    function PeriodController($scope, $location, oConstantsService, oAuthService, oPeriodService,$translate) {
        this.m_oScope = $scope;
        this.m_oLocation  = $location;
        this.m_oConstantsService = oConstantsService;
        this.m_oAuthService = oAuthService;
        this.m_oScope.m_oController = this;
        this.m_oPeriodService = oPeriodService;
        this.m_oTranslateService = $translate;
        var oController = this;

        if (oConstantsService.isUserLogged())
        {
            if (oConstantsService.getUser() != null)
            {
                // only administrator can manage users
                if (oConstantsService.canUserUseDateSelect())
                {
                    //Load users
                    oPeriodService.loadPeriods();
                }
            }
        }

        this.m_oPeriodService.setAsUnchanged();

        $scope.$on('$locationChangeStart', function (event, next, current) {

            if (oController.m_oPeriodService.isModified()) {
                var bAnswer = confirm("Are you sure you want to leave this page without saving?");
                if (!bAnswer) {
                    event.preventDefault();
                }
            }
        });


    }

    PeriodController.prototype.getPeriods = function() {
        return this.m_oPeriodService.getPeriods();

    };

    PeriodController.prototype.savePeriods = function() {

        var oController = this;
        var oPeriods =  oController.m_oPeriodService.getPeriods();
        var lastPeriod = oPeriods[oPeriods.length - 1];
        if (oPeriods != null)
        {
            if (lastPeriod.timestampStart != null && lastPeriod.timestampStart != '' &&
                lastPeriod.timestampEnd != null && lastPeriod.timestampEnd != '')
            {

                if (lastPeriod.timestampStart > lastPeriod.timestampEnd)
                {
                    oController.m_oTranslateService('PERIOD_CHECKDATA').then(function(msg) {
                        vex.dialog.alert({
                            message: msg
                        });
                    });

                    return;
                }

                //save last user
                oController.m_oPeriodService.savePeriods(lastPeriod).success(function(data){

                    //load user
                    oController.m_oPeriodService.loadPeriods();
                    oController.m_oTranslateService('PERIOD_SAVED').then(function(msg) {
                        vex.dialog.alert({
                            message: msg
                        });
                    });
                });
            }
            else
            {
                oController.m_oTranslateService('PERIOD_FIELDMANDATORY').then(function(msg) {
                    vex.dialog.alert({
                        message: msg
                    });
                });
            }
        }

    };

    PeriodController.prototype.addPeriod = function() {

        var oController = this;
        var oPeriods =  oController.m_oPeriodService.getPeriods();

        if (oPeriods.length == 0 || !oController.m_oPeriodService.isModified())
        {
            oController.m_oPeriodService.setAsModified();

            //add new user
            var oPeriod = {
                timestampStart: '',
                timestampEnd: ''
            };

            //add to user list
            oPeriods.push(oPeriod);
        }
        else {
            //take last user
            var lastPeriod = oPeriods[oPeriods.length - 1];
            if (lastPeriod != null) {
                if (lastPeriod.timestampStart != null && lastPeriod.timestampStart != '' &&
                    lastPeriod.timestampEnd != null && lastPeriod.timestampEnd != '') {
                    //save last user
                    oController.m_oPeriodService.savePeriods(lastPeriod).success(function (data) {

                        //load user
                        oController.m_oPeriodService.loadPeriods();
                        oController.loadPeriods();

                        oController.m_oPeriodService.setAsModified();
                        //add new user
                        var oPeriod = {
                            timestampStart: '',
                            timestampEnd: ''
                        };

                        //add to user list
                        oPeriods.push(oPeriod);

                    });

                }
            }
        }

    };

    PeriodController.prototype.deletePeriod = function(id) {

        var oController = this;

        oController.m_oTranslateService('PERIOD_DELETEASK').then(function(msg) {

            vex.dialog.confirm({
                message: msg,
                callback: function (value) {
                    if (value) {
                        //save last user
                        oController.m_oPeriodService.deletePeriod(id).success(function (data) {

                            //load user
                            oController.m_oPeriodService.loadPeriods();
                            oController.loadPeriods();

                        });
                    }
                }

            });
        });

    };

    PeriodController.$inject = [
        '$scope',
        '$location',
        'ConstantsService',
        'AuthService',
        'PeriodService',
        '$translate'
    ];

    return PeriodController;
}) ();

