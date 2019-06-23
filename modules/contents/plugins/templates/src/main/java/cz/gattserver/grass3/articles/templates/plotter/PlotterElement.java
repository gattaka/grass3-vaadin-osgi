package cz.gattserver.grass3.articles.templates.plotter;

import java.util.UUID;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class PlotterElement implements Element {

	private double startx;
	private double endx;
	private double starty;
	private double endy;
	private String function;

	private String width;
	private String height;

	protected UUID generateRandomUUID() {
		return UUID.randomUUID();
	}

	public PlotterElement(double startx, double endx, double starty, double endy, String function, String width,
			String height) {
		this.startx = startx;
		this.endx = endx;
		this.starty = starty;
		this.endy = endy;
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
		ctx.addJSCode("$.plotter" + id + "=function(){let e,t,l,n,i,o,r,f,a=document.getElementById(\"can" + id
				+ "\"),s=a.getContext(\"2d\"),c=a.offsetWidth,u=a.offsetHeight,h=!1,y=" + startx + "," + "d=" + endx
				+ ",g=" + starty + ",m=" + endy
				+ ",v=y,k=d,M=g,S=m,L=Math.ceil(u/50),T=Math.ceil(c/50),b=function(x){return " + function
				+ "},x=function(e){return Number(e.toPrecision(2))},E=function(){s.clearRect(0,0,c,u),s.strokeStyle=\"grey\",s.fillStyle=\"grey\";s.font=\"13px Monospace\",s.strokeRect(0,0,c,u);l=Math.abs(y-d),n=Math.abs(g-m),r=u/(o=n*(1+2/T)),f=c/(i=l*(1+2/L));let e=(i-l)/2,t=(o-n)/2,a=Math.min(y,d)-e,h=Math.max(y,d)+e,v=i/c;if(v<=0||a>h)return void console.log(\"Plotter error, infinite cycle detected!\");let k=Math.min(g,m)-t,M=(Math.max(g,m),u+k*r),S=-a*f,E=x(o/L);(L<1||L==1/0)&&(L=1);let p=Math.floor(k/E)*E;s.setLineDash([5,5]),s.strokeStyle=\"lightgrey\",s.fillStyle=\"grey\",s.textAlign=\"left\";for(let e=0;e<=L;e++){let t=E*e+p,l=M-t*r;s.beginPath(),s.moveTo(0,l),s.lineTo(c,l),s.stroke();let n=l-8;s.fillText(x(t),S+8,n)}s.setLineDash([]),s.strokeStyle=\"grey\",s.fillStyle=\"grey\",s.beginPath(),s.moveTo(0,M),s.lineTo(c,M),s.stroke();let D=x(i/T);(T<1||T==1/0)&&(T=1);let P=Math.floor(a/D)*D;s.setLineDash([5,5]),s.strokeStyle=\"lightgrey\",s.fillStyle=\"grey\";for(let e=0;e<=T;e++){let t=D*e+P,l=S+t*f,n=l+8;s.beginPath(),s.moveTo(l,0),s.lineTo(l,u),s.stroke(),s.textAlign=\"left\",s.fillText(x(t),n,M-8)}s.setLineDash([]),s.strokeStyle=\"grey\",s.fillStyle=\"grey\",s.beginPath(),s.moveTo(S,0),s.lineTo(S,u),s.stroke();for(let e=a;e<=h;e+=v){let t=e*f+S,l=M-b(e)*r;s.fillStyle=\"blue\",s.fillRect(t-1.5,l-1.5,3,3)}};return a.addEventListener(\"wheel\",function(e){e.preventDefault();let t=e.deltaY>0?1.1:1/1.1;y*=t,d*=t,g*=t,m*=t,E()}),a.addEventListener(\"mousedown\",function(l){h=!0,e=l.clientX,t=l.clientY},!1),a.addEventListener(\"mouseup\",function(e){h=!1},!1),a.addEventListener(\"mouseleave\",function(e){h=!1},!1),a.addEventListener(\"mousemove\",function(l){if(!h)return;let n=(e-l.clientX)/f,i=(t-l.clientY)/r;y+=n,d+=n,g-=i,m-=i,E(),e=l.clientX,t=l.clientY},!1),a.addEventListener(\"dblclick\",function(e){e.preventDefault(),y=v,d=k,g=M,m=S,E()},!1),{start:function(){E()}}}(),$.plotter"
				+ id + ".start();");
	}
}
