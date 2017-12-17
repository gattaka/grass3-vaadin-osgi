package cz.gattserver.grass3.perf;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Aspect
//@Component
public class PerformanceAspect {

	Logger logger = LoggerFactory.getLogger(StopWatch.DEFAULT_LOGGER_NAME);

	@Around("execution(* cz.gattserver.grass3..*.*(..)) && !execution(* cz.gattserver.grass3.GrassUI.*(..))")
	public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {

		String methodName = joinPoint.getSignature().getName();
		String className = joinPoint.getTarget().getClass().getName();
		StopWatch stopWatch = new StopWatch(className + "#" + methodName);

		try {
			return joinPoint.proceed();
		} catch (IllegalArgumentException e) {
			logger.info(methodName + " throw an exception");
			throw e;
		} finally {
			String log = stopWatch.stop();
			logger.info(log);
		}
	}

}
