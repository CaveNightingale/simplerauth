package com.dqu.simplerauth.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Collections;

@Mixin(GameProfileArgumentType.class)
public class GameProfileArgumentTypeMixin {
	/**
	 * @author CaveNightingale
	 * @reason Fail to inject
	 */
	@Overwrite
	public GameProfileArgumentType.GameProfileArgument parse(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '@') {
			EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
			EntitySelector entitySelector = entitySelectorReader.read();
			if (entitySelector.includesNonPlayers()) {
				throw EntityArgumentType.PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.create();
			} else {
				return new GameProfileArgumentType.SelectorBacked(entitySelector);
			}
		} else {
			int entitySelectorReader = stringReader.getCursor();

			while(stringReader.canRead() && stringReader.peek() != ' ') {
				stringReader.skip();
			}

			String entitySelector = stringReader.getString().substring(entitySelectorReader, stringReader.getCursor());
			return (source) -> Collections.singleton(new GameProfile(PlayerEntity.getOfflinePlayerUuid(entitySelector), entitySelector));
		}
	}
}
