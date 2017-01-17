var CookieManager = (function () {
    function CookieManager() {
    }
    // *** Cookie - Accessibility mode ***
    //<editor-fold desc="Cookie - Accessibility mode ">
    CookieManager.setAccessibilityModeActive = function () {
        CookieManager.setCookie(CookieManager.COOKIES.ACCESSIBILITY_MODE, "true", CookieManager.COOKIE_EXPIRE_TIME_DAYS);
    };
    CookieManager.setAccessibilityModeNotActive = function () {
        CookieManager.deleteCookie(CookieManager.COOKIES.ACCESSIBILITY_MODE);
    };
    CookieManager.isAccessibilityModeActive = function () {
        var bVal = CookieManager.getCookie(CookieManager.COOKIES.ACCESSIBILITY_MODE);
        return (bVal != null);
    };
    //</editor-fold>
    CookieManager.deleteCookie = function (cname) {
        CookieManager.setCookie(cname, "", -1000);
    };
    CookieManager.setCookie = function (cname, cvalue, exdays) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
        var expires = "expires=" + d.toUTCString();
        document.cookie = cname + "=" + cvalue + "; " + expires;
    };
    CookieManager.getCookie = function (cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ')
                c = c.substring(1);
            if (c.indexOf(name) == 0)
                return c.substring(name.length, c.length);
        }
        return null;
    };
    CookieManager.COOKIE_EXPIRE_TIME_DAYS = 30;
    CookieManager.COOKIES = {
        ACCESSIBILITY_MODE: "accessibility_mode"
    };
    return CookieManager;
}());
//# sourceMappingURL=CookieManager.js.map