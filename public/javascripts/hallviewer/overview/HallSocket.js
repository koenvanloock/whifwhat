var HallOverview;
(function (HallOverview) {
    var HallSocket = (function () {
        function HallSocket(hallReference, base, alertService) {
            console.log(base.ws);
            var hall = hallReference;
            this.webSocket = new WebSocket(base.ws + '/activehall');
        }
        return HallSocket;
    }());
    HallOverview.HallSocket = HallSocket;
})(HallOverview || (HallOverview = {}));
