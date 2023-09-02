package net.sf.l2j.gameserver.scripting.quests;

import net.sf.l2j.gameserver.enums.actors.ClassRace;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;

public class Q101_SwordOfSolidarity extends Quest
{
	private static final String qn = "Q101_SwordOfSolidarity";
	
	// NPCs
	private static final int ROIEN = 30008;
	private static final int ALTRAN = 30283;
	
	// Items
	private static final int BROKEN_SWORD_HANDLE = 739;
	private static final int BROKEN_BLADE_BOTTOM = 740;
	private static final int BROKEN_BLADE_TOP = 741;
	private static final int ROIEN_LETTER = 796;
	private static final int DIRECTIONS_TO_RUINS = 937;
	private static final int ALTRAN_NOTE = 742;
	
	private static final int SWORD_OF_SOLIDARITY = 738;
	private static final int LESSER_HEALING_POT = 1060;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	
	public Q101_SwordOfSolidarity()
	{
		super(101, "Sword of Solidarity");
		
		setItemsIds(BROKEN_SWORD_HANDLE, BROKEN_BLADE_BOTTOM, BROKEN_BLADE_TOP, ROIEN_LETTER, DIRECTIONS_TO_RUINS, ALTRAN_NOTE);
		
		addStartNpc(ROIEN);
		addTalkId(ROIEN, ALTRAN);
		
		addKillId(20361, 20362);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30008-03.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(ROIEN_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("30283-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(ROIEN_LETTER, 1);
			st.giveItems(DIRECTIONS_TO_RUINS, 1);
		}
		else if (event.equalsIgnoreCase("30283-06.htm"))
		{
			st.takeItems(BROKEN_SWORD_HANDLE, 1);
			st.giveItems(SWORD_OF_SOLIDARITY, 1);
			
			st.rewardNewbieShots(7000, 0);
			st.rewardItems(LESSER_HEALING_POT, 100);
			st.rewardItems(ECHO_BATTLE, 10);
			st.rewardItems(ECHO_LOVE, 10);
			st.rewardItems(ECHO_SOLITUDE, 10);
			st.rewardItems(ECHO_FEAST, 10);
			st.rewardItems(ECHO_CELEBRATION, 10);
			
			player.broadcastPacket(new SocialAction(player, 3));
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(false);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getRace() != ClassRace.HUMAN)
					htmltext = "30008-01a.htm";
				else if (player.getStatus().getLevel() < 9)
					htmltext = "30008-01.htm";
				else
					htmltext = "30008-02.htm";
				break;
			
			case STATE_STARTED:
				int cond = (st.getInt("cond"));
				switch (npc.getNpcId())
				{
					case ROIEN:
						if (cond == 1)
							htmltext = "30008-04.htm";
						else if (cond == 2)
							htmltext = "30008-03a.htm";
						else if (cond == 3)
							htmltext = "30008-06.htm";
						else if (cond == 4)
						{
							htmltext = "30008-05.htm";
							st.set("cond", "5");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ALTRAN_NOTE, 1);
							st.giveItems(BROKEN_SWORD_HANDLE, 1);
						}
						else if (cond == 5)
							htmltext = "30008-05a.htm";
						break;
					
					case ALTRAN:
						if (cond == 1)
							htmltext = "30283-01.htm";
						else if (cond == 2)
							htmltext = "30283-03.htm";
						else if (cond == 3)
						{
							htmltext = "30283-04.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(DIRECTIONS_TO_RUINS, 1);
							st.takeItems(BROKEN_BLADE_TOP, 1);
							st.takeItems(BROKEN_BLADE_BOTTOM, 1);
							st.giveItems(ALTRAN_NOTE, 1);
						}
						else if (cond == 4)
							htmltext = "30283-04a.htm";
						else if (cond == 5)
							htmltext = "30283-05.htm";
						break;
				}
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerCondition(player, npc, "cond", "2");
		if (st == null)
			return null;
		
		if (!st.hasQuestItems(BROKEN_BLADE_TOP))
			st.dropItems(BROKEN_BLADE_TOP, 1, 1, 200000);
		else if (st.dropItems(BROKEN_BLADE_BOTTOM, 1, 1, 200000))
			st.set("cond", "3");
		
		return null;
	}
}