package net.sf.l2j.gameserver.skills.funcs;

import net.sf.l2j.gameserver.enums.Paperdoll;
import net.sf.l2j.gameserver.enums.skills.Stats;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.skills.L2Skill;
import net.sf.l2j.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncPDefMod extends Func
{
	private static final FuncPDefMod INSTANCE = new FuncPDefMod();
	
	private FuncPDefMod()
	{
		super(null, Stats.POWER_DEFENCE, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		if (effector instanceof Player)
		{
			final Player player = (Player) effector;
			final boolean isMage = player.isMageClass();
			
			if (player.getInventory().hasItemIn(Paperdoll.HEAD))
				value -= 12;
			
			if (player.getInventory().hasItemIn(Paperdoll.CHEST))
				value -= (isMage) ? 15 : 31;
			
			if (player.getInventory().hasItemIn(Paperdoll.LEGS))
				value -= (isMage) ? 8 : 18;
			
			if (player.getInventory().hasItemIn(Paperdoll.GLOVES))
				value -= 8;
			
			if (player.getInventory().hasItemIn(Paperdoll.FEET))
				value -= 7;
		}
		return value * effector.getStatus().getLevelMod();
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}