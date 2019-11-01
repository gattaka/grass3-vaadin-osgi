package cz.gattserver.grass3.services.impl;

import java.util.Random;

import org.springframework.stereotype.Service;

import cz.gattserver.grass3.services.RandomSourceService;

@Service("randomSourceServiceImpl")
public class RandomSourceServiceImpl implements RandomSourceService {

	@Override
	public long getRandomLong(long range) {
		return (long) Math.floor(Math.random() * range);
	}

	@Override
	public int getRandomInt(int range) {
		return new Random().nextInt(range);
	}

}
