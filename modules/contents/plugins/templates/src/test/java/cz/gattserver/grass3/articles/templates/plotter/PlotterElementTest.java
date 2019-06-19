package cz.gattserver.grass3.articles.templates.plotter;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class PlotterElementTest {

	@Test
	public void test() {
		PlotterElement e = new PlotterElement(1.1, 2.2, 3.2, 45.5, "sin(x) * 2") {
			@Override
			protected UUID generateRandomUUID() {
				return UUID.fromString("8e6f402c-5432-4dca-9de7-f787f863fa20");
			}
		};
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals(
				"<canvas id=\"can8e6f402c_5432_4dca_9de7_f787f863fa20\" width=\"700\" height=\"300\" style=\"cursor: pointer\"></canvas>",
				out);
		assertEquals(1, ctx.getJSCodes().size());
		assertEquals(
				"$.plotter8e6f402c_5432_4dca_9de7_f787f863fa20=function(){let e,t,l,n,i,o,r=document.getElementById(\"can8e6f402c_5432_4dca_9de7_f787f863fa20\"),"
						+ "a=r.getContext(\"2d\"),c=r.width,s=r.height,f=!1,h=1.1,y=2.2,g=3.2,u=45.5,d=function(x){return sin(x) * 2},m=function(){a.clearRect(0,0,c,s),"
						+ "a.strokeStyle=\"grey\",a.fillStyle=\"grey\";a.font=\"13px Monospace\",a.strokeRect(0,0,c,s);l=1.01*Math.abs(h-y),n=1.01*Math.abs(g-u),i=s/n,o=c/l;"
						+ "let e=Math.min(h,y),t=Math.max(h,y),r=l/c;if(r<=0||e>t)return void console.log(\"Plotter error, infinite cycle detected!\");let f=Math.min(g,u),"
						+ "m=(Math.max(g,u),s+Math.min(g,u)*i),M=-Math.min(h,y)*o,v=8,S=n/v,k=S>10?10*Math.ceil(S/10):Math.ceil(S);((v=Math.ceil(n/k))<1||v==1/0)&&(v=1);"
						+ "let T=Math.floor(f/k)*k;a.setLineDash([5,5]),a.strokeStyle=\"lightgrey\",a.fillStyle=\"grey\",a.textAlign=\"left\";for(let e=0;e<=v;e++){let t=k*e+T,"
						+ "l=m-t*i;if(l<-21||l>s+21)continue;a.beginPath(),a.moveTo(0,l),a.lineTo(c,l),a.stroke();let n=l-8;a.fillText(t,M+8,n)}a.setLineDash([]),a.strokeStyle"
						+ "=\"grey\",a.fillStyle=\"grey\",a.beginPath(),a.moveTo(0,m),a.lineTo(c,m),a.stroke();let L=8,x=l/L,b=x>10?10*Math.ceil(x/10):Math.ceil(x);((L=Math."
						+ "ceil(l/b))<1||L==1/0)&&(L=1);let E=Math.floor(e/b)*b;a.setLineDash([5,5]),a.strokeStyle=\"lightgrey\",a.fillStyle=\"grey\";for(let e=0;e<=L;e++){"
						+ "let t=b*e+E,l=M+t*o;if(l<-21||l>c+21)continue;let n=l+8;a.beginPath(),a.moveTo(l,0),a.lineTo(l,s),a.stroke(),a.textAlign=\"left\",a.fillText(t,n,m-8)}"
						+ "a.setLineDash([]),a.strokeStyle=\"grey\",a.fillStyle=\"grey\",a.beginPath(),a.moveTo(M,0),a.lineTo(M,s),a.stroke();for(let l=e;l<=t;l+=r){let e=l*o+M,"
						+ "t=m-d(l)*i;a.fillStyle=\"blue\",a.fillRect(e-1.5,t-1.5,3,3)}};return r.addEventListener(\"wheel\",function(e){let t=e.deltaY>0?1.1:1/1.1;h*=t,y*=t,"
						+ "g*=t,u*=t,m()}),r.addEventListener(\"mousedown\",function(l){f=!0,e=l.clientX,t=l.clientY},!1),r.addEventListener(\"mouseup\",function(e){f=!1},!1),"
						+ "r.addEventListener(\"mouseleave\",function(e){f=!1},!1),r.addEventListener(\"mousemove\",function(l){if(!f)return;let n=(e-l.clientX)/o,r=(t-l.clientY)/i;"
						+ "h+=n,y+=n,g-=r,u-=r,m(),e=l.clientX,t=l.clientY},!1),{start:function(){m()}}}(),$.plotter8e6f402c_5432_4dca_9de7_f787f863fa20.start();",
				ctx.getJSCodes().iterator().next());
	}

}
