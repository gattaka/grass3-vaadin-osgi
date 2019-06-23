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
		assertEquals(
				"$.plotter" + validUuid + "=function(){let e,t,l,n,i,o,r,f,a=document.getElementById(\"can" + validUuid
						+ "\"),s=a.getContext(\"2d\"),c=a.offsetWidth,u=a.offsetHeight,h=!1,y=-5.0,d=6.0,g=-2.0,m=5.0,v=y,k=d,M=g,S=m,L=Math.ceil(u/50),T=Math.ceil(c/50),b=function(x){return 1/x},x=function(e){return Number(e.toPrecision(2))},E=function(){s.clearRect(0,0,c,u),s.strokeStyle=\"grey\",s.fillStyle=\"grey\";s.font=\"13px Monospace\",s.strokeRect(0,0,c,u);l=Math.abs(y-d),n=Math.abs(g-m),r=u/(o=n*(1+2/T)),f=c/(i=l*(1+2/L));let e=(i-l)/2,t=(o-n)/2,a=Math.min(y,d)-e,h=Math.max(y,d)+e,v=i/c;if(v<=0||a>h)return void console.log(\"Plotter error, infinite cycle detected!\");let k=Math.min(g,m)-t,M=(Math.max(g,m),u+k*r),S=-a*f,E=x(o/L);(L<1||L==1/0)&&(L=1);let p=Math.floor(k/E)*E;s.setLineDash([5,5]),s.strokeStyle=\"lightgrey\",s.fillStyle=\"grey\",s.textAlign=\"left\";for(let e=0;e<=L;e++){let t=E*e+p,l=M-t*r;s.beginPath(),s.moveTo(0,l),s.lineTo(c,l),s.stroke();let n=l-8;s.fillText(x(t),S+8,n)}s.setLineDash([]),s.strokeStyle=\"grey\",s.fillStyle=\"grey\",s.beginPath(),s.moveTo(0,M),s.lineTo(c,M),s.stroke();let D=x(i/T);(T<1||T==1/0)&&(T=1);let P=Math.floor(a/D)*D;s.setLineDash([5,5]),s.strokeStyle=\"lightgrey\",s.fillStyle=\"grey\";for(let e=0;e<=T;e++){let t=D*e+P,l=S+t*f,n=l+8;s.beginPath(),s.moveTo(l,0),s.lineTo(l,u),s.stroke(),s.textAlign=\"left\",s.fillText(x(t),n,M-8)}s.setLineDash([]),s.strokeStyle=\"grey\",s.fillStyle=\"grey\",s.beginPath(),s.moveTo(S,0),s.lineTo(S,u),s.stroke();for(let e=a;e<=h;e+=v){let t=e*f+S,l=M-b(e)*r;s.fillStyle=\"blue\",s.fillRect(t-1.5,l-1.5,3,3)}};return a.addEventListener(\"wheel\",function(e){e.preventDefault();let t=e.deltaY>0?1.1:1/1.1;y*=t,d*=t,g*=t,m*=t,E()}),a.addEventListener(\"mousedown\",function(l){h=!0,e=l.clientX,t=l.clientY},!1),a.addEventListener(\"mouseup\",function(e){h=!1},!1),a.addEventListener(\"mouseleave\",function(e){h=!1},!1),a.addEventListener(\"mousemove\",function(l){if(!h)return;let n=(e-l.clientX)/f,i=(t-l.clientY)/r;y+=n,d+=n,g-=i,m-=i,E(),e=l.clientX,t=l.clientY},!1),a.addEventListener(\"dblclick\",function(e){e.preventDefault(),y=v,d=k,g=M,m=S,E()},!1),{start:function(){E()}}}(),$.plotter"
						+ validUuid + ".start();",
				ctx.getJSCodes().iterator().next());
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
