
class WebcamDialog
{
    private static OPEN_DIALOGS_COUNT = 0;
    private static DIALOGS_OFFSET_H = 100;


    private static m_openDialogsImages : Object = {};

    public static add(oWebcamObj, onClose = null)
    {
        var webcamCode = "webcamdialog" + new Date().getTime();
        var dialogId = webcamCode;
        var container = $("#webcam-dialogs-container");

        var html = "<div id='"+ dialogId +"' title='Basic dialog'><p>This is the default dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'x' icon.</p></div>";
        var newDialog = $(html);
        var dialogImage = $("<img src='https://www.google.it/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png'/>");
        newDialog.append(dialogImage);

        newDialog.appendTo(container);
        newDialog.dialog({
            position: {
                my: "left top",
                at: "left+"+ (WebcamDialog.DIALOGS_OFFSET_H * WebcamDialog.OPEN_DIALOGS_COUNT) +" top",
                of: container
            },
            create: function( event, ui ) {
                WebcamDialog.OPEN_DIALOGS_COUNT++;
                WebcamDialog.m_openDialogsImages[dialogId] = dialogImage;
            },
            close: function( event, ui ) {
                WebcamDialog.OPEN_DIALOGS_COUNT--;
                if( onClose && typeof onClose == "function")
                {
                    onClose();
                }
            },
            resizeStart: function (event, ui) {
                container.show();
            },
            resizeStop: function (event, ui) {
                container.hide();
            }
        });

        return dialogId;
    }

    public static refreshWebcamImage(webcamCode)
    {
        if( WebcamDialog.m_openDialogsImages[webcamCode] )
        {
            WebcamDialog.m_openDialogsImages[webcamCode].attr("src", "http://www.w3schools.com/images/colorpicker.gif");
        }
    }


    //public static open(oWebcamObj)
    //{
    //    oWebcamObj = {
    //        value : 10
    //    };
    //    vex.dialog.open({
    //        message : "<h3>Webcam Image</h3>",
    //        input: "<div id='aaaa' style='font-size: 1.2rem; width: 600px; height: 300px;'></div>",
    //        afterOpen: function($vexContent) {
    //
    //            $($vexContent[0]).addClass("webcam-dialog");
    //
    //            setInterval(function(){
    //                $('#aaaa').html(oWebcamObj.value++);
    //            }, 1000);
    //
    //            //return $vexContent.append($el);
    //        },
    //        buttons: [
    //            // Register as 'outside Genova' skater
    //            $.extend({}, vex.dialog.buttons.OK, {
    //                className: 'vex-dialog-button-location-not-ge',
    //                text: 'CHIUDI',
    //                click: function($vexContent, event) {
    //                    $vexContent.data().vex.value = false;
    //                    vex.close($vexContent.data().vex.id);
    //                }
    //            }),
    //            //// Register as 'Zenaroller' skater
    //            //$.extend({}, vex.dialog.buttons.OK, {
    //            //    className: 'vex-dialog-button-location-ge',
    //            //    text: 'SI',
    //            //    click: function($vexContent, event) {
    //            //        $vexContent.data().vex.value = LOCATION_GENOVA;
    //            //        vex.close($vexContent.data().vex.id);
    //            //    }
    //            //}),
    //        ],
    //        callback: function(value) {
    //            if( value == false)
    //            {
    //
    //            }
    //            else
    //            {
    //
    //            }
    //        }
    //    })
    //}
}