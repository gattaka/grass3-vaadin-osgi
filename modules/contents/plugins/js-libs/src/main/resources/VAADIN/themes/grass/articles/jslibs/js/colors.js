var $ = $ || {};
$.GJSLibColors = {
	hslToColor: function(h, s, l) {		
		return "hsl(" + (255 * h) + ", " + (s * 100) + "%, " + (l * 50) + "%)";
	}
};