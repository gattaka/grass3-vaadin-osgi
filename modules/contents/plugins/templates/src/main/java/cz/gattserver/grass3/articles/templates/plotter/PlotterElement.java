package cz.gattserver.grass3.articles.templates.plotter;

import java.util.UUID;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class PlotterElement implements Element {

	private static final int WIDTH = 700;

	private double startx;
	private double endx;
	private double starty;
	private double endy;
	private String function;

	public PlotterElement(double startx, double endx, double starty, double endy, String function) {
		this.startx = startx;
		this.endx = endx;
		this.starty = starty;
		this.endy = endy;
		this.function = function;
	}

	@Override
	public void apply(Context ctx) {
		String id = UUID.randomUUID().toString().replaceAll("-", "_");
		ctx.print("<canvas id=\"can" + id + "\" width=\"" + WIDTH
				+ "\" height=\"300\" style=\"cursor: pointer\"></canvas>");
		// minified https://javascript-minifier.com/
		ctx.addJSCode("$.plotter" + id + "=function(){let e,t,l,n,i,o,r=document.getElementById(\"can" + id
				+ "\"),a=r.getContext(\"2d\"),c=r.width,s=r.height,f=!1," + "h=" + startx + ",y=" + endx + ",g="
				+ starty + ",u=" + endy + ",d=function(x){return " + function
				+ "},m=function(){a.clearRect(0,0,c,s),a.strokeStyle=\"grey\","
				+ "a.fillStyle=\"grey\";a.font=\"13px Monospace\",a.strokeRect(0,0,c,s);l=1.01*Math.abs(h-y),"
				+ "n=1.01*Math.abs(g-u),i=s/n,o=c/l;let e=Math.min(h,y),t=Math.max(h,y),r=l/c;if(r<=0||e>t)return "
				+ "void console.log(\"Plotter error, infinite cycle detected!\");let f=Math.min(g,u),m=(Math.max(g,u),"
				+ "s+Math.min(g,u)*i),M=-Math.min(h,y)*o,v=8,S=n/v,k=S>10?10*Math.ceil(S/10):Math.ceil(S);((v=Math.ceil(n/k))<1||v==1/0)&&(v=1);"
				+ "let T=Math.floor(f/k)*k;a.setLineDash([5,5]),a.strokeStyle=\"lightgrey\",a.fillStyle=\"grey\",a.textAlign=\"left\";for(let e=0;e<=v;e++)"
				+ "{let t=k*e+T,l=m-t*i;if(l<-21||l>s+21)continue;a.beginPath(),a.moveTo(0,l),a.lineTo(c,l),a.stroke();let n=l-8;a.fillText(t,M+8,n)}"
				+ "a.setLineDash([]),a.strokeStyle=\"grey\",a.fillStyle=\"grey\",a.beginPath(),a.moveTo(0,m),a.lineTo(c,m),a.stroke();"
				+ "let L=8,x=l/L,b=x>10?10*Math.ceil(x/10):Math.ceil(x);((L=Math.ceil(l/b))<1||L==1/0)&&(L=1);let E=Math.floor(e/b)*b;"
				+ "a.setLineDash([5,5]),a.strokeStyle=\"lightgrey\",a.fillStyle=\"grey\";for(let e=0;e<=L;e++){let t=b*e+E,l=M+t*o;"
				+ "if(l<-21||l>c+21)continue;let n=l+8;a.beginPath(),a.moveTo(l,0),a.lineTo(l,s),a.stroke(),a.textAlign=\"left\",a.fillText(t,n,m-8)}"
				+ "a.setLineDash([]),a.strokeStyle=\"grey\",a.fillStyle=\"grey\",a.beginPath(),a.moveTo(M,0),a.lineTo(M,s),a.stroke();"
				+ "for(let l=e;l<=t;l+=r){let e=l*o+M,t=m-d(l)*i;a.fillStyle=\"blue\",a.fillRect(e-1.5,t-1.5,3,3)}};return r.addEventListener(\"wheel\","
				+ "function(e){let t=e.deltaY>0?1.1:1/1.1;h*=t,y*=t,g*=t,u*=t,m()}),r.addEventListener(\"mousedown\",function(l){f=!0,e=l.clientX,t=l.clientY},!1),"
				+ "r.addEventListener(\"mouseup\",function(e){f=!1},!1),r.addEventListener(\"mouseleave\",function(e){f=!1},!1),r.addEventListener(\"mousemove\","
				+ "function(l){if(!f)return;let n=(e-l.clientX)/o,r=(t-l.clientY)/i;h+=n,y+=n,g-=r,u-=r,m(),e=l.clientX,t=l.clientY},!1),{start:function(){m()}}}(),"
				+ "$.plotter" + id + ".start();");
	}
}
