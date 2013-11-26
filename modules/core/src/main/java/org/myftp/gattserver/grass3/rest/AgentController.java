package org.myftp.gattserver.grass3.rest;

import org.myftp.gattserver.grass3.facades.IQuotesFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/agent")
public class AgentController {

	@Autowired
	private IQuotesFacade iQuotesFacade;

	@RequestMapping("test")
	@ResponseBody
	public String getTest() {
		return "test 2";
	}

	@RequestMapping(value = "quote", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getQuote() {
		return iQuotesFacade.getRandomQuote();
	}

	public AgentController() {
		System.out.println("AgentController online");
	}

}
