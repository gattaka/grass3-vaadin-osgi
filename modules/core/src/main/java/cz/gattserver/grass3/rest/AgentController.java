package cz.gattserver.grass3.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass3.facades.QuotesFacade;

@Controller
@RequestMapping("/agent")
public class AgentController {

	private static Logger logger = LoggerFactory.getLogger(AgentController.class);

	@Autowired
	private QuotesFacade iQuotesFacade;

	@RequestMapping("ping")
	@ResponseBody
	public String getPing() {
		return "ok";
	}

	@RequestMapping(value = "quote", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getQuote() {
		return iQuotesFacade.getRandomQuote();
	}

	public AgentController() {
		logger.info("AgentController online");
	}

}