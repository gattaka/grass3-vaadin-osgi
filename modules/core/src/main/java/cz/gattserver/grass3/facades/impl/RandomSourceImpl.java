package cz.gattserver.grass3.facades.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.facades.RandomSource;

@Component
public class RandomSourceImpl implements RandomSource {

	@Override
	public long getRandomNumber(long range) {
		return (long) Math.floor(Math.random() * range);
	}

}
