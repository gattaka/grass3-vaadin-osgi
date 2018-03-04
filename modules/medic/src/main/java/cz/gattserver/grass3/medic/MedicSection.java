package cz.gattserver.grass3.medic;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

@Component("medicSection")
public class MedicSection implements SectionService {

	private static Logger logger = LoggerFactory.getLogger(MedicSection.class);

	private static final long ONCE_PER_DAY = 1L * 1000 * 60 * 60 * 24;

	@Resource(name = "medicPageFactory")
	private PageFactory medicPageFactory;

	@Autowired
	private MedicEmailNotifier emailNotifier;

	@PostConstruct
	private final void init() {
		logger.info("MedicSection init");
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
		return medicPageFactory;
	}

	public String getSectionCaption() {
		return "Medic";
	}

}
