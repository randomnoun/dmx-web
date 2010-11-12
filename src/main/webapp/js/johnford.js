// this javascript was modified from 
//   http://johnford.is/examples/script.aculo.us/slider-mouse-wheel.html
// by knoxg on 2010-11-12




// mouse wheel code from http://adomas.org/javascript-mouse-wheel/
//function handle(delta) {
//    slider.setValueBy(-delta);
//}

/** Event handler for mouse wheel event. */
function fncWheelHandler(event) {
    var slider = this;
    var args = $A(arguments); args.shift();
    var multiplier = args[0] ? args[0] : 1;
    var delta = 0;
    if (!event) /* For IE. */
        event = window.event;
    if (event.wheelDelta) { /* IE/Opera. */
        delta = event.wheelDelta/120;
        /** In Opera 9, delta differs in sign as compared to IE. */
        if (window.opera)
            delta = -delta;
    } else if (event.detail) { /** Mozilla case. */
        /** In Mozilla, sign of delta is different than in IE.
        * Also, delta is multiple of 3.
        */
        delta = -event.detail/3;
    }

    /** If delta is nonzero, handle it.
    * Basically, delta is now positive if wheel was scrolled up,
    * and negative, if wheel was scrolled down.
    */
    if (delta) { slider.setValueBy(-delta * multiplier); }

    /** Prevent default actions caused by mouse wheel.
    * That might be ugly, but we handle scrolls somehow
    * anyway, so don't bother here..
    */
    if (event.preventDefault)
        event.preventDefault();
    
    event.returnValue = false;
}
