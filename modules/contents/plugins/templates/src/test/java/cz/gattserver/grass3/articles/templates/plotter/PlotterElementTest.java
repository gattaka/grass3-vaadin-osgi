package cz.gattserver.grass3.articles.templates.plotter;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class PlotterElementTest {

	@Test
	public void test() {
		PlotterElement e = new PlotterElement(1.1, 2.2, 3.2, 45.5, "sin(x) * 2", null, null) {
			@Override
			protected UUID generateRandomUUID() {
				return UUID.fromString("8e6f402c-5432-4dca-9de7-f787f863fa20");
			}
		};
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals(
				"<div style=\"margin: 0px 5px; display: inline-block;\"><canvas id=\"can8e6f402c_5432_4dca_9de7_f787f863fa20\" style=\"cursor: pointer; \"></canvas></div>",
				out);
		assertEquals(1, ctx.getJSCodes().size());
		assertEquals(
				"$.plotter8e6f402c_5432_4dca_9de7_f787f863fa20=function(){let e,t,l,n,i,o,r,f,a=document.getElementById(\"can8e6f402c_5432_4dca_9de7_f787f863fa20\"),s=a.getContext(\"2d\"),c=a.offsetWidth,u=a.offsetHeight,h=!1,y=1.1,d=2.2,g=3.2,m=45.5,v=y,k=d,M=g,S=m,L=Math.ceil(u/50),T=Math.ceil(c/50),b=function(x){return sin(x) * 2},x=function(e){return Number(e.toPrecision(2))},E=function(){s.clearRect(0,0,c,u),s.strokeStyle=\"grey\",s.fillStyle=\"grey\";s.font=\"13px Monospace\",s.strokeRect(0,0,c,u);l=Math.abs(y-d),n=Math.abs(g-m),r=u/(o=n*(1+2/T)),f=c/(i=l*(1+2/L));let e=(i-l)/2,t=(o-n)/2,a=Math.min(y,d)-e,h=Math.max(y,d)+e,v=i/c;if(v<=0||a>h)return void console.log(\"Plotter error, infinite cycle detected!\");let k=Math.min(g,m)-t,M=(Math.max(g,m),u+k*r),S=-a*f,E=x(o/L);(L<1||L==1/0)&&(L=1);let p=Math.floor(k/E)*E;s.setLineDash([5,5]),s.strokeStyle=\"lightgrey\",s.fillStyle=\"grey\",s.textAlign=\"left\";for(let e=0;e<=L;e++){let t=E*e+p,l=M-t*r;s.beginPath(),s.moveTo(0,l),s.lineTo(c,l),s.stroke();let n=l-8;s.fillText(x(t),S+8,n)}s.setLineDash([]),s.strokeStyle=\"grey\",s.fillStyle=\"grey\",s.beginPath(),s.moveTo(0,M),s.lineTo(c,M),s.stroke();let D=x(i/T);(T<1||T==1/0)&&(T=1);let P=Math.floor(a/D)*D;s.setLineDash([5,5]),s.strokeStyle=\"lightgrey\",s.fillStyle=\"grey\";for(let e=0;e<=T;e++){let t=D*e+P,l=S+t*f,n=l+8;s.beginPath(),s.moveTo(l,0),s.lineTo(l,u),s.stroke(),s.textAlign=\"left\",s.fillText(x(t),n,M-8)}s.setLineDash([]),s.strokeStyle=\"grey\",s.fillStyle=\"grey\",s.beginPath(),s.moveTo(S,0),s.lineTo(S,u),s.stroke();for(let e=a;e<=h;e+=v){let t=e*f+S,l=M-b(e)*r;s.fillStyle=\"blue\",s.fillRect(t-1.5,l-1.5,3,3)}};return a.addEventListener(\"wheel\",function(e){e.preventDefault();let t=e.deltaY>0?1.1:1/1.1;y*=t,d*=t,g*=t,m*=t,E()}),a.addEventListener(\"mousedown\",function(l){h=!0,e=l.clientX,t=l.clientY},!1),a.addEventListener(\"mouseup\",function(e){h=!1},!1),a.addEventListener(\"mouseleave\",function(e){h=!1},!1),a.addEventListener(\"mousemove\",function(l){if(!h)return;let n=(e-l.clientX)/f,i=(t-l.clientY)/r;y+=n,d+=n,g-=i,m-=i,E(),e=l.clientX,t=l.clientY},!1),a.addEventListener(\"dblclick\",function(e){e.preventDefault(),y=v,d=k,g=M,m=S,E()},!1),{start:function(){E()}}}(),$.plotter8e6f402c_5432_4dca_9de7_f787f863fa20.start();",
				ctx.getJSCodes().iterator().next());
	}

}
