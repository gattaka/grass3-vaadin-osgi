package cz.gattserver.grass3.articles.templates.plotter;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.UUID;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class PlotterParserTest {

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
		parsingProcessor.nextToken(); // musí se inicializovat
		return parsingProcessor;
	}

	@Test
	public void test() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER]1/x;-5;6;-2;5[/PLOTTER]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		String uuidText = ctx.getOutput().substring(68, 104).replace("_", "-");
		UUID uuid = UUID.fromString(uuidText);
		String validUuid = uuid.toString().replaceAll("-", "_");
		assertEquals("<div style=\"margin: 0px 5px; display: inline-block;\"><canvas id=\"can" + validUuid
				+ "\" style=\"cursor: pointer; \"></canvas></div>", ctx.getOutput());
		assertEquals(1, ctx.getJSCodes().size());
		assertEquals("$.plotter" + validUuid + "=function(){let e,t,l,n,i,o,r=document.getElementById(\"can" + validUuid
				+ "\"),a=r.getContext(\"2d\"),f=r.offsetWidth,s=r.offsetHeight,c=!1,u=-5.0,h=6.0,y=-2.0,d=5.0,g=u,m=h,v=y,M=d,k=function(x){return 1/x},S=function(e){return Number(e.toPrecision(2))},L=function(){a.clearRect(0,0,f,s),a.strokeStyle=\"grey\",a.fillStyle=\"grey\";a.font=\"13px Monospace\",a.strokeRect(0,0,f,s);l=1.01*Math.abs(u-h),n=1.01*Math.abs(y-d),i=s/n,o=f/l;let e=Math.min(u,h),t=Math.max(u,h),r=l/f;if(r<=0||e>t)return void console.log(\"Plotter error, infinite cycle detected!\");let c=Math.min(y,d),g=(Math.max(y,d),s+Math.min(y,d)*i),m=-Math.min(u,h)*o,v=8,M=S(n/v),L=Math.ceil(n/M);L>v?v++:L<v&&v--,(v<1||v==1/0)&&(v=1);let T=Math.floor(c/M)*M;a.setLineDash([5,5]),a.strokeStyle=\"lightgrey\",a.fillStyle=\"grey\",a.textAlign=\"left\";for(let e=0;e<=v;e++){let t=M*e+T,l=g-t*i;if(l<-21||l>s+21)continue;a.beginPath(),a.moveTo(0,l),a.lineTo(f,l),a.stroke();let n=l-8;a.fillText(S(t),m+8,n)}a.setLineDash([]),a.strokeStyle=\"grey\",a.fillStyle=\"grey\",a.beginPath(),a.moveTo(0,g),a.lineTo(f,g),a.stroke();let b=8,x=S(l/b);Math.ceil(l/x)>b?b++:L<b&&b--,(b<1||b==1/0)&&(b=1);let E=Math.floor(e/x)*x;a.setLineDash([5,5]),a.strokeStyle=\"lightgrey\",a.fillStyle=\"grey\";for(let e=0;e<=b;e++){let t=x*e+E,l=m+t*o;if(l<-21||l>f+21)continue;let n=l+8;a.beginPath(),a.moveTo(l,0),a.lineTo(l,s),a.stroke(),a.textAlign=\"left\",a.fillText(S(t),n,g-8)}a.setLineDash([]),a.strokeStyle=\"grey\",a.fillStyle=\"grey\",a.beginPath(),a.moveTo(m,0),a.lineTo(m,s),a.stroke();for(let l=e;l<=t;l+=r){let e=l*o+m,t=g-k(l)*i;a.fillStyle=\"blue\",a.fillRect(e-1.5,t-1.5,3,3)}};return r.addEventListener(\"wheel\",function(e){e.preventDefault();let t=e.deltaY>0?1.1:1/1.1;u*=t,h*=t,y*=t,d*=t,L()}),r.addEventListener(\"mousedown\",function(l){c=!0,e=l.clientX,t=l.clientY},!1),r.addEventListener(\"mouseup\",function(e){c=!1},!1),r.addEventListener(\"mouseleave\",function(e){c=!1},!1),r.addEventListener(\"mousemove\",function(l){if(!c)return;let n=(e-l.clientX)/o,r=(t-l.clientY)/i;u+=n,h+=n,y-=r,d-=r,L(),e=l.clientX,t=l.clientY},!1),r.addEventListener(\"dblclick\",function(e){u=g,h=m,y=v,d=M,L()},!1),{start:function(){L()}}}(),$.plotter"
				+ validUuid + ".start();", ctx.getJSCodes().iterator().next());
	}

	@Test(expected = TokenException.class)
	public void test_failAbbrEOF() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER]1/x;-5;6;-2;5[PLOTTER]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failTitleEOF() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER]1/x;-5;6;-2;5"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = ParserException.class)
	public void test_failMandatoryContent() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER][PLOTTER]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = ParserException.class)
	public void test_failMandatoryContent2() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER] [PLOTTER]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = ParserException.class)
	public void test_failBadContent() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER]45565 222 [PLOTTER]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = ParserException.class)
	public void test_failMissingParts() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER]func;a;c[PLOTTER]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = ParserException.class)
	public void test_failExtraParts() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER]func;a;c;5;5;s;2[PLOTTER]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = ParserException.class)
	public void test_failBadFunction() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER]1/y;-5;6;-2;5[PLOTTER]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = ParserException.class)
	public void test_failBadNumber1() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER]1/x;-5q;6;-2;5[PLOTTER]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = ParserException.class)
	public void test_failBadNumber2() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER]1/x;-5;6q;-2;5[PLOTTER]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = ParserException.class)
	public void test_failBadNumber3() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER]1/x;-5;6;-2q;5[PLOTTER]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = ParserException.class)
	public void test_failBadNumber4() {
		PlotterParser parser = new PlotterParser("PLOTTER");
		Element element = parser.parse(getParsingProcessorWithText("[PLOTTER]1/x;-5;6;-2;5q[PLOTTER]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

}
