/*
 * Rollover Class for making it easier to do image rollover effects
 * 
 * How to use it:
 * new Rollover('#myElemId');
 * <img id='myElemId' src='image.png' />  will hover to: <img src='image-over.png' />
 * 
 * new Rollover('#myElemId', {background: true});
 * <div id='myElemId'></div>
 * div#myElemId {
 *   background-image: url(/images/bg.png);    -- will hover to: "bg-over.png"
 * }
 * 
 */
var Rollover= Class.create();
Rollover.prototype = {
	initialize: function(selector, opts){
	
		this.options = {
			image: false,
			background: false
		};
		Object.extend(this.options, opts || {});
		
		this.over_img = new Image();
		this.out_img = new Image();
		
		this.elem = $$(selector).first();
		if (this.options['image']) {
			this.out_img_src = this.options['image'];
		}
		else 
			if (this.options['background']) {
				this.out_img_src = this.elem.background - image;
			}
			else {
				this.out_img_src = this.elem.src ? this.elem.src : '';
			}
		this.over_img_src = this.out_img_src.replace(/^(.*)\.(jpg|png|jpeg|gif(\?.*)?)/, "$1-over.$2");
		
		// preload rollover images
		this.out_img.src = this.out_img_src;
		this.over_img.src = this.over_img_src;
		
		this.elem.observe('mouseover', this.onmouseover.bindAsEventListener(this));
		this.elem.observe('mouseout', this.onmouseout.bindAsEventListener(this));
	},
	onmouseover: function(e) {
		if (this.options['background']) {
			Event.element(e).setStyle({
				'background-image': "url(" + this.over_img.src + ")"
			});
		}
		else {
			Event.element(e).src = this.over_img.src;
		}
	},
	onmouseout: function(e) {
		if (this.options['background']) {
			Event.element(e).setStyle({
				'background-image': "url(" + this.out_img.src + ")"
			});
		}
		else {
			Event.element(e).src = this.out_img.src;
		}
	}
}

