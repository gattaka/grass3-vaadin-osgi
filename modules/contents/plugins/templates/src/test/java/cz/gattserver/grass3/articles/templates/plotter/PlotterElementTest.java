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
				"$.plotter8e6f402c_5432_4dca_9de7_f787f863fa20=function(){let e,t,l,n,i,o,r=document.getElementById(\"can8e6f402c_5432_4dca_9de7_f787f863fa20\"),a=r.getContext(\"2d\"),f=r.offsetWidth,s=r.offsetHeight,c=!1,u=1.1,h=2.2,y=3.2,d=45.5,g=u,m=h,v=y,M=d,k=function(x){return sin(x) * 2},S=function(e){return Number(e.toPrecision(2))},L=function(){a.clearRect(0,0,f,s),a.strokeStyle=\"grey\",a.fillStyle=\"grey\";a.font=\"13px Monospace\",a.strokeRect(0,0,f,s);l=1.01*Math.abs(u-h),n=1.01*Math.abs(y-d),i=s/n,o=f/l;let e=Math.min(u,h),t=Math.max(u,h),r=l/f;if(r<=0||e>t)return void console.log(\"Plotter error, infinite cycle detected!\");let c=Math.min(y,d),g=(Math.max(y,d),s+Math.min(y,d)*i),m=-Math.min(u,h)*o,v=8,M=S(n/v),L=Math.ceil(n/M);L>v?v++:L<v&&v--,(v<1||v==1/0)&&(v=1);let T=Math.floor(c/M)*M;a.setLineDash([5,5]),a.strokeStyle=\"lightgrey\",a.fillStyle=\"grey\",a.textAlign=\"left\";for(let e=0;e<=v;e++){let t=M*e+T,l=g-t*i;if(l<-21||l>s+21)continue;a.beginPath(),a.moveTo(0,l),a.lineTo(f,l),a.stroke();let n=l-8;a.fillText(S(t),m+8,n)}a.setLineDash([]),a.strokeStyle=\"grey\",a.fillStyle=\"grey\",a.beginPath(),a.moveTo(0,g),a.lineTo(f,g),a.stroke();let b=8,x=S(l/b);Math.ceil(l/x)>b?b++:L<b&&b--,(b<1||b==1/0)&&(b=1);let E=Math.floor(e/x)*x;a.setLineDash([5,5]),a.strokeStyle=\"lightgrey\",a.fillStyle=\"grey\";for(let e=0;e<=b;e++){let t=x*e+E,l=m+t*o;if(l<-21||l>f+21)continue;let n=l+8;a.beginPath(),a.moveTo(l,0),a.lineTo(l,s),a.stroke(),a.textAlign=\"left\",a.fillText(S(t),n,g-8)}a.setLineDash([]),a.strokeStyle=\"grey\",a.fillStyle=\"grey\",a.beginPath(),a.moveTo(m,0),a.lineTo(m,s),a.stroke();for(let l=e;l<=t;l+=r){let e=l*o+m,t=g-k(l)*i;a.fillStyle=\"blue\",a.fillRect(e-1.5,t-1.5,3,3)}};return r.addEventListener(\"wheel\",function(e){e.preventDefault();let t=e.deltaY>0?1.1:1/1.1;u*=t,h*=t,y*=t,d*=t,L()}),r.addEventListener(\"mousedown\",function(l){c=!0,e=l.clientX,t=l.clientY},!1),r.addEventListener(\"mouseup\",function(e){c=!1},!1),r.addEventListener(\"mouseleave\",function(e){c=!1},!1),r.addEventListener(\"mousemove\",function(l){if(!c)return;let n=(e-l.clientX)/o,r=(t-l.clientY)/i;u+=n,h+=n,y-=r,d-=r,L(),e=l.clientX,t=l.clientY},!1),r.addEventListener(\"dblclick\",function(e){u=g,h=m,y=v,d=M,L()},!1),{start:function(){L()}}}(),$.plotter8e6f402c_5432_4dca_9de7_f787f863fa20.start();",
				ctx.getJSCodes().iterator().next());
	}

}
