class CookieManager
{
    private static COOKIE_EXPIRE_TIME_DAYS = 30;
    private static COOKIES = {
        ACCESSIBILITY_MODE : "accessibility_mode",
    }



    // *** Cookie - Accessibility mode ***
    //<editor-fold desc="Cookie - Accessibility mode ">
    public static setAccessibilityModeActive()
    {
        CookieManager.setCookie(CookieManager.COOKIES.ACCESSIBILITY_MODE, "true", CookieManager.COOKIE_EXPIRE_TIME_DAYS);
    }

    public static setAccessibilityModeNotActive() {
        CookieManager.deleteCookie(CookieManager.COOKIES.ACCESSIBILITY_MODE)
    }

    public static isAccessibilityModeActive()
    {
        var bVal = CookieManager.getCookie(CookieManager.COOKIES.ACCESSIBILITY_MODE);
        return (bVal != null);
    }
    //</editor-fold>



    private static deleteCookie(cname)
    {
        CookieManager.setCookie(cname, "", -1000);
    }

    private static setCookie(cname, cvalue, exdays) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays*24*60*60*1000));
        var expires = "expires="+d.toUTCString();
        document.cookie = cname + "=" + cvalue + "; " + expires;
    }

    private static getCookie(cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for(var i=0; i<ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1);
            if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
        }
        return null;
    }
}
