var WebcamDialog = (function () {
    function WebcamDialog() {
    }
    WebcamDialog.add = function (oWebcamObj, onClose) {

        if (onClose === void 0) { onClose = null; }
        var dialogId = oWebcamObj.attributes.shortCode;
        //return if already open
        if (WebcamDialog.m_openDialogsImages[dialogId])
            return dialogId;
        var container = $("#webcam-dialogs-container");
        var html = "<div id='" + dialogId + "' class='webcam-dialog' title='"+ oWebcamObj.attributes.name +"'></div>";
        var newDialog = $(html);
        var dialogImage = $("<img src='" + oWebcamObj.attributes.imgPath + "' onerror=\"this.src='img/nodata.jpg'\">");
        //var dialogImage = $("<img src='img/webcam/webcam-panesi_std_0847.png'/>");
        newDialog.append(dialogImage);
        //newDialog.appendTo(container);
        newDialog.dialog({
            width:"auto",
            height:"400",
            resizable: true,
            position: {
                my: "left top",
                at: "left+" + (WebcamDialog.DIALOGS_OFFSET_H * WebcamDialog.OPEN_DIALOGS_COUNT) + " top",
                of: container
            },
            create: function (event, ui) {
                WebcamDialog.OPEN_DIALOGS_COUNT++;
                WebcamDialog.m_openDialogsImages[dialogId] = dialogImage;
            },
            close: function (event, ui) {
                WebcamDialog.OPEN_DIALOGS_COUNT--;
                WebcamDialog.m_openDialogsImages[dialogId] = null;
                if (onClose && typeof onClose == "function") {
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
    };
    WebcamDialog.refreshWebcamImage = function (webcamCode, img) {
        if (WebcamDialog.m_openDialogsImages[webcamCode]) {
            WebcamDialog.m_openDialogsImages[webcamCode].attr("src", img);
        }
    };
    WebcamDialog.OPEN_DIALOGS_COUNT = 0;
    WebcamDialog.DIALOGS_OFFSET_H = 100;
    WebcamDialog.m_openDialogsImages = {};
    return WebcamDialog;
})();
//# sourceMappingURL=WebcamDialog.js.map