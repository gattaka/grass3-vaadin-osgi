package cz.gattserver.grass3.articles.templates.plotter;

import java.util.UUID;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class PlotterElement implements Element {

	private double xUnit;
	private double yUnit;
	private double xCenter;
	private double yCenter;
	private String function;

	private String width;
	private String height;

	protected UUID generateRandomUUID() {
		return UUID.randomUUID();
	}

	public PlotterElement(double xUnit, double yUnit, double xCenter, double yCenter, String function, String width,
			String height) {
		this.xUnit = xUnit;
		this.yUnit = yUnit;
		this.xCenter = xCenter;
		this.yCenter = yCenter;
		this.function = function;
		this.width = width;
		this.height = height;
	}

	@Override
	public void apply(Context ctx) {
		String id = generateRandomUUID().toString().replaceAll("-", "_");
		ctx.print("<div style=\"margin: 0px 5px; display: inline-block;\"><canvas id=\"can" + id
				+ "\" style=\"cursor: pointer; \"");
		if (width != null)
			ctx.print(" width=\"" + width + "\" ");
		if (height != null)
			ctx.print(" height=\"" + height + "\" ");
		ctx.print("></canvas></div>");
		// minified https://javascript-minifier.com/

		ctx.addJSCode("$.plotter" + id + "=function(){let e,t,n,l,o,i,r,f,c=document.getElementById(\"can" + id
				+ "\"),u=c.getContext(\"2d\"),a=c.offsetWidth,s=c.offsetHeight,d=!1,g=" + xUnit + ",v=" + yUnit
				+ ",h=g,y=v,m=Math.ceil(a/50),T=Math.ceil(s/50),k=function(x){return " + function
				+ "},b=function(){n=m*g,l=T*v,o=50/g,i=50/v},p=function(){b(),g=h,v=y,r=-" + xCenter + "*o,f=" + yCenter
				+ "*i,M()},E=function(e){return Number(e.toPrecision(2))},M=function(){u.clearRect(0,0,a,s),u.strokeStyle=\"grey\",u.fillStyle=\"grey\";u.font=\"11px Monospace\",u.strokeRect(0,0,a,s);b();let e=Math.floor(r/50),t=Math.floor(f/50),c=r%50,d=f%50;c<0&&(c+=50),d<0&&(d+=50);let h=-n/2-e*g,y=h+n,p=-l/2+t*v,M=-h*o+c,S=s+p*i+d;u.strokeStyle=\"#eeeeee\",u.fillStyle=\"grey\",u.textAlign=\"left\";let x=Math.floor(m/2)+e,L=M-50*x;for(let e=-1;e<=m;e++){let t=g*(e-x),n=L+50*e,l=n+6;u.beginPath(),u.moveTo(n,0),u.lineTo(n,s),u.stroke(),u.textAlign=\"left\",u.fillText(E(t),l,S-6)}let P=t-Math.floor(T/2),Y=S-50*P;for(let e=0;e<=T;e++){let t=v*(P+e),n=Y-50*e;u.beginPath(),u.moveTo(0,n),u.lineTo(a,n),u.stroke();let l=n-6;u.fillText(E(t),M+6,l)}u.strokeStyle=\"grey\",u.fillStyle=\"grey\",u.beginPath(),u.moveTo(0,S),u.lineTo(a,S),u.stroke(),u.beginPath(),u.moveTo(M,0),u.lineTo(M,s),u.stroke();let R=1/o;for(let e=h-g;e<=y;e+=R){let t=e*o+M,n=S-k(e)*i;u.fillStyle=\"blue\",u.fillRect(t-1,n-1,2,2)}};return c.addEventListener(\"wheel\",function(e){e.preventDefault();e.deltaY>0?(g*=1.1,v*=1.1):(g/=1.1,v/=1.1),M()}),c.addEventListener(\"mousedown\",function(n){d=!0,e=n.clientX,t=n.clientY},!1),c.addEventListener(\"mouseup\",function(e){d=!1},!1),c.addEventListener(\"mouseleave\",function(e){d=!1},!1),c.addEventListener(\"mousemove\",function(n){d&&(r-=e-n.clientX,f-=t-n.clientY,e=n.clientX,t=n.clientY,M())},!1),c.addEventListener(\"dblclick\",function(e){e.preventDefault(),p()},!1),{start:function(){p()}}}(),$.plotter"
				+ id + ".start();");

	}
}
