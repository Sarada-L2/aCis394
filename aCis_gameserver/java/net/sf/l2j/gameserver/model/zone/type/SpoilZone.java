package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.type.subtype.SpawnZoneType;

/**
 * @author Sarada
 */

public class SpoilZone extends SpawnZoneType
{
	public SpoilZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (character instanceof Player)
		{
			final Player player = (Player) character;
			if (!(player.getClassId() == ClassId.BOUNTY_HUNTER) && !(player.getClassId() == ClassId.FORTUNE_SEEKER))
			{
				player.sendMessage("You kicked in Spoil Zone, Only Spoil!");
				return;
			}
			character.setInsideZone(ZoneId.SPOIL, true);
			return;
		}
		
	}
	
	@Override
	protected void onExit(Creature character)
	{
		
		if (character instanceof Player)
		{
			final Player activeChar = (Player) character;
			activeChar.setInsideZone(ZoneId.SPOIL, false);
			return;
		}
	}
	
}