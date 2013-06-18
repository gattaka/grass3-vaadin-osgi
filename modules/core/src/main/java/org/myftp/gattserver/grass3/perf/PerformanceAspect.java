package org.myftp.gattserver.grass3.perf;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

	Logger logger = LoggerFactory.getLogger(StopWatch.DEFAULT_LOGGER_NAME);

	@Around("execution(* org.myftp.gattserver.grass3..*.*(..)) && !execution(* org.myftp.gattserver.grass3.GrassUI.*(..))")
	public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {

		String methodName = joinPoint.getSignature().getName();
		String className = joinPoint.getTarget().getClass().getName();
		StopWatch stopWatch = new StopWatch(className + "#" + methodName);

		try {
			Object result = joinPoint.proceed();
			return result;
		} catch (IllegalArgumentException e) {
			logger.info(methodName + " throw an exception");
			throw e;
		} finally {
			String log = stopWatch.stop();
//			System.out.println(log);
			logger.info(log);
		}
	}

}
