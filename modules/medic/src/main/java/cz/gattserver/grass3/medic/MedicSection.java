package cz.gattserver.grass3.medic;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

@Component("medicSection")
public class MedicSection implements SectionService {

	@Resource(name = "medicPageFactory")
	private PageFactory medicPageFactory;

	@Autowired
	private EmailNotifier emailNotifier;

	private static final long ONCE_PER_DAY = 1L * 1000 * 60 * 60 * 24;
	private static final int ONE_DAY = 1;
	private static final int FOUR_AM = 4;
	private static final int ZERO_MINUTES = 0;

	private static Date getTomorrowMorning4am() {
		Calendar tomorrow = new GregorianCalendar();
		tomorrow.add(Calendar.DATE, ONE_DAY);
		Calendar result = new GregorianCalendar(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH),
				tomorrow.get(Calendar.DATE), FOUR_AM, ZERO_MINUTES);
		return result.getTime();
	}

	@PostConstruct
	private void initEmailNotifier() {
		TimerTask fetchMail = emailNotifier.getTimerTask();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(fetchMail, getTomorrowMorning4am(), ONCE_PER_DAY);
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
