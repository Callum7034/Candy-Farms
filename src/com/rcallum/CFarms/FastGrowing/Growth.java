package com.rcallum.CFarms.FastGrowing;

import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

public class Growth {
	private static Growth instance;

	public Growth() {
		instance = this;
	}

	public static Growth getInstance() {
		if (instance == null) {
			instance = new Growth();
		}
		return instance;
	}
	
	public int StageToInt(CropState state) {
		int stage = 0;
		if (state == CropState.SEEDED) stage = 0;
		if (state == CropState.GERMINATED) stage = 1;
		if (state == CropState.VERY_SMALL) stage = 2;
		if (state == CropState.SMALL) stage = 3;
		if (state == CropState.MEDIUM) stage = 4;
		if (state == CropState.TALL) stage = 5;
		if (state == CropState.VERY_TALL) stage = 6;
		if (state == CropState.RIPE) stage = 7;
		return stage;
	}
	
	public CropState IntToState(int stage) {
		if (stage == 0) return CropState.SEEDED;
		if (stage == 1) return CropState.GERMINATED;
		if (stage == 2) return CropState.VERY_SMALL;
		if (stage == 3) return CropState.SMALL;
		if (stage == 4) return CropState.MEDIUM;
		if (stage == 5) return CropState.TALL;
		if (stage == 6) return CropState.VERY_TALL;
		if (stage >= 7) return CropState.RIPE;
		return CropState.SEEDED;
	}
	
	public CropState getNextState(CropState current) {
		int next = StageToInt(current);
		return IntToState(next + 1);
	}
	
	public int getState(Location loc) {
		BlockState state = loc.getBlock().getState();
		MaterialData data = state.getData();
	
		if (data instanceof Crops) {
			CropState c = Growth.getInstance().getNextState(((Crops) data).getState());
			return StageToInt(c);
		}
		return -1;
	}
}
