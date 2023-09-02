package net.sf.l2j.gameserver.scripting.quests;

import net.sf.l2j.gameserver.enums.actors.ClassRace;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;

public class Q107_MercilessPunishment extends Quest
{
	private static final String qn = "Q107_MercilessPunishment";
	
	// NPCs
	private static final int HATOS = 30568;
	private static final int PARUGON = 30580;
	
	// Items
	private static final int HATOS_ORDER_1 = 1553;
	private static final int HATOS_ORDER_2 = 1554;
	private static final int HATOS_ORDER_3 = 1555;
	private static final int LETTER_TO_HUMAN = 1557;
	private static final int LETTER_TO_DARKELF = 1556;
	private static final int LETTER_TO_ELF = 1558;
	
	// Rewards
	private static final int LESSER_HEALING_POTION = 1060;
	private static final int BUTCHER_SWORD = 1510;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	
	public Q107_MercilessPunishment()
	{
		super(107, "Merciless Punishment");
		
		setItemsIds(HATOS_ORDER_1, HATOS_ORDER_2, HATOS_ORDER_3, LETTER_TO_HUMAN, LETTER_TO_DARKELF, LETTER_TO_ELF);
		
		addStartNpc(HATOS);
		addTalkId(HATOS, PARUGON);
		
		addKillId(27041); // Baranka's Messenger
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = event;
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30568-03.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(HATOS_ORDER_1, 1);
		}
		else if (event.equalsIgnoreCase("30568-06.htm"))
		{
			st.playSound(QuestState.SOUND_GIVEUP);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30568-07.htm"))
		{
			st.set("cond", "4");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(HATOS_ORDER_1, 1);
			st.giveItems(HATOS_ORDER_2, 1);
		}
		else if (event.equalsIgnoreCase("30568-09.htm"))
		{
			st.set("cond", "6");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(HATOS_ORDER_2, 1);
			st.giveItems(HATOS_ORDER_3, 1);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getRace() != ClassRace.ORC)
					htmltext = "30568-00.htm";
				else if (player.getStatus().getLevel() < 12)
					htmltext = "30568-01.htm";
				else
					htmltext = "30568-02.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case HATOS:
						if (cond == 1 || cond == 2)
							htmltext = "30568-04.htm";
						else if (cond == 3)
							htmltext = "30568-05.htm";
						else if (cond == 4 || cond == 6)
							htmltext = "30568-09.htm";
						else if (cond == 5)
							htmltext = "30568-08.htm";
						else if (cond == 7)
						{
							htmltext = "30568-10.htm";
							st.takeItems(HATOS_ORDER_3, -1);
							st.takeItems(LETTER_TO_DARKELF, -1);
							st.takeItems(LETTER_TO_HUMAN, -1);
							st.takeItems(LETTER_TO_ELF, -1);
							st.giveItems(BUTCHER_SWORD, 1);
							
							st.rewardNewbieShots(7000, 0);
							st.rewardItems(LESSER_HEALING_POTION, 100);
							st.rewardItems(ECHO_BATTLE, 10);
							st.rewardItems(ECHO_LOVE, 10);
							st.rewardItems(ECHO_SOLITUDE, 10);
							st.rewardItems(ECHO_FEAST, 10);
							st.rewardItems(ECHO_CELEBRATION, 10);
							
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case PARUGON:
						htmltext = "30580-01.htm";
						if (cond == 1)
						{
							st.set("cond", "2");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
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
		
		final QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		int cond = st.getInt("cond");
		
		if (cond == 2)
		{
			st.set("cond", "3");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(LETTER_TO_HUMAN, 1);
		}
		else if (cond == 4)
		{
			st.set("cond", "5");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(LETTER_TO_DARKELF, 1);
		}
		else if (cond == 6)
		{
			st.set("cond", "7");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(LETTER_TO_ELF, 1);
		}
		
		return null;
	}
}