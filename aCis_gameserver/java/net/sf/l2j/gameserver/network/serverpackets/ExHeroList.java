package net.sf.l2j.gameserver.network.serverpackets;

import java.util.Collection;

import net.sf.l2j.commons.util.StatsSet;

import net.sf.l2j.gameserver.data.manager.HeroManager;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;

public class ExHeroList extends L2GameServerPacket
{
	private final Collection<StatsSet> _heroList;
	
	public ExHeroList()
	{
		_heroList = HeroManager.getInstance().getHeroes().values();
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x23);
		writeD(_heroList.size());
		
		for (StatsSet hero : _heroList)
		{
			writeS(hero.getString(Olympiad.CHAR_NAME));
			writeD(hero.getInteger(Olympiad.CLASS_ID));
			writeS(hero.getString(HeroManager.CLAN_NAME, ""));
			writeD(hero.getInteger(HeroManager.CLAN_CREST, 0));
			writeS(hero.getString(HeroManager.ALLY_NAME, ""));
			writeD(hero.getInteger(HeroManager.ALLY_CREST, 0));
			writeD(hero.getInteger(HeroManager.COUNT));
		}
	}
}