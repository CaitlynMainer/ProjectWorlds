package com.gmail.trentech.pjw.utils;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

public enum Gamemode {

	SURVIVAL(0, GameModes.SURVIVAL), CREATIVE(1, GameModes.CREATIVE), ADVENTURE(2, GameModes.ADVENTURE), SPECTATOR(3, GameModes.SPECTATOR);

	int index;
	GameMode gamemode;

	private Gamemode(int index, GameMode gamemode) {
		this.index = index;
		this.gamemode = gamemode;
	}

	public int getIndex() {
		return index;
	}

	public GameMode getGameMode() {
		return gamemode;
	}

	public static Optional<GameMode> get(String name) {
		Optional<GameMode> optional = Optional.empty();

		Gamemode[] gamemodes = Gamemode.values();

		for (Gamemode gamemode : gamemodes) {
			if (gamemode.getGameMode().getName().equals(name)) {
				optional = Optional.of(gamemode.getGameMode());
				break;
			}
		}

		return optional;
	}

	public static Optional<GameMode> get(int index) {
		Optional<GameMode> optional = Optional.empty();

		Gamemode[] gamemodes = Gamemode.values();

		for (Gamemode gamemode : gamemodes) {
			if (gamemode.getIndex() == index) {
				optional = Optional.of(gamemode.getGameMode());
				break;
			}
		}

		return optional;
	}
}
