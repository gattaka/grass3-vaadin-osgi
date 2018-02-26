package cz.gattserver.grass3.monitor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

@Component("monitorSection")
public class MonitorSection implements SectionService {

	@Resource(name = "monitorPageFactory")
	private PageFactory monitorPageFactory;

	@Autowired
	private MonitorEmailNotifier emailNotifier;

	private static final long ONCE_PER_DAY = 1L * 1000 * 60 * 60 * 24;

	@PostConstruct
	private void initEmailNotifier() {
		TimerTask fetchMail = emailNotifier.getTimerTask();
		Timer timer = new Timer();
		LocalDateTime ldt = LocalDateTime.now().plusDays(1).withHour(4);
		Date tomorrowMorning4am = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		timer.scheduleAtFixedRate(fetchMail, tomorrowMorning4am, ONCE_PER_DAY);
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN);
	}

	public PageFactory getSectionPageFactory() {
		return monitorPageFactory;
	}

	public String getSectionCaption() {
		return "System";
	}

}
