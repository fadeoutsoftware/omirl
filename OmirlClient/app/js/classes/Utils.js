/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var Utils = (function ()
{
    function Utils() {
    }
    
    // Static methods    
    Utils.emptyArray = function (aArray)
    {
        if( Array.isArray(aArray))
            aArray.splice(0, aArray.length);
        else
            throw("Not an array");
    };
    
    
    return Utils;
})();
