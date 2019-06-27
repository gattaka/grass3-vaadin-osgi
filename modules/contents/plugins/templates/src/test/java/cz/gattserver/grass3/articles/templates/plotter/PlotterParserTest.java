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
				"$.plotter" + validUuid + "=function(){let e,t,n,l,o,i,r,f,c=document.getElementById(\"can" + validUuid
						+ "\"),u=c.getContext(\"2d\"),a=c.offsetWidth,s=c.offsetHeight,d=!1,g=-5.0,v=6.0,h=g,y=v,m=Math.ceil(a/50),T=Math.ceil(s/50),k=function(x){return 1/x},b=function(){n=m*g,l=T*v,o=50/g,i=50/v},p=function(){b(),g=h,v=y,r=--2.0*o,f=5.0*i,M()},E=function(e){return Number(e.toPrecision(2))},M=function(){u.clearRect(0,0,a,s),u.strokeStyle=\"grey\",u.fillStyle=\"grey\";u.font=\"11px Monospace\",u.strokeRect(0,0,a,s);b();let e=Math.floor(r/50),t=Math.floor(f/50),c=r%50,d=f%50;c<0&&(c+=50),d<0&&(d+=50);let h=-n/2-e*g,y=h+n,p=-l/2+t*v,M=-h*o+c,S=s+p*i+d;u.strokeStyle=\"#eeeeee\",u.fillStyle=\"grey\",u.textAlign=\"left\";let x=Math.floor(m/2)+e,L=M-50*x;for(let e=-1;e<=m;e++){let t=g*(e-x),n=L+50*e,l=n+6;u.beginPath(),u.moveTo(n,0),u.lineTo(n,s),u.stroke(),u.textAlign=\"left\",u.fillText(E(t),l,S-6)}let P=t-Math.floor(T/2),Y=S-50*P;for(let e=0;e<=T;e++){let t=v*(P+e),n=Y-50*e;u.beginPath(),u.moveTo(0,n),u.lineTo(a,n),u.stroke();let l=n-6;u.fillText(E(t),M+6,l)}u.strokeStyle=\"grey\",u.fillStyle=\"grey\",u.beginPath(),u.moveTo(0,S),u.lineTo(a,S),u.stroke(),u.beginPath(),u.moveTo(M,0),u.lineTo(M,s),u.stroke();let R=1/o;for(let e=h-g;e<=y;e+=R){let t=e*o+M,n=S-k(e)*i;u.fillStyle=\"blue\",u.fillRect(t-1,n-1,2,2)}};return c.addEventListener(\"wheel\",function(e){e.preventDefault();e.deltaY>0?(g*=1.1,v*=1.1):(g/=1.1,v/=1.1),M()}),c.addEventListener(\"mousedown\",function(n){d=!0,e=n.clientX,t=n.clientY},!1),c.addEventListener(\"mouseup\",function(e){d=!1},!1),c.addEventListener(\"mouseleave\",function(e){d=!1},!1),c.addEventListener(\"mousemove\",function(n){d&&(r-=e-n.clientX,f-=t-n.clientY,e=n.clientX,t=n.clientY,M())},!1),c.addEventListener(\"dblclick\",function(e){e.preventDefault(),p()},!1),{start:function(){p()}}}(),$.plotter"
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
