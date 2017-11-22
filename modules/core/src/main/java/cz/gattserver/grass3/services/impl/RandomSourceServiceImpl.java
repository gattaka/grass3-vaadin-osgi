package cz.gattserver.grass3.services.impl;

import org.springframework.stereotype.Service;

import cz.gattserver.grass3.services.RandomSourceService;

@Service("randomSourceImpl")
public class RandomSourceServiceImpl implements RandomSourceService {

	@Override
	public long getRandomNumber(long range) {
		return (long) Math.floor(Math.random() * range);
	}

}
