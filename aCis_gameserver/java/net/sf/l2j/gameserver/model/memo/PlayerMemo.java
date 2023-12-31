package net.sf.l2j.gameserver.model.memo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;

/**
 * An implementation of {@link AbstractMemo} used for Player. There is a restore/save system.
 */
@SuppressWarnings("serial")
public class PlayerMemo extends AbstractMemo
{
	private static final CLogger LOGGER = new CLogger(PlayerMemo.class.getName());
	
	private static final String SELECT_QUERY = "SELECT * FROM character_memo WHERE charId = ?";
	private static final String DELETE_QUERY = "DELETE FROM character_memo WHERE charId = ?";
	private static final String INSERT_QUERY = "INSERT INTO character_memo (charId, var, val) VALUES (?, ?, ?)";
	
	private final int _objectId;
	
	public PlayerMemo(int objectId)
	{
		_objectId = objectId;
		restoreMe();
	}
	
	@Override
	public boolean restoreMe()
	{
		// Restore previous variables.
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(SELECT_QUERY))
			{
				ps.setInt(1, _objectId);
				
				try (ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
						set(rs.getString("var"), rs.getString("val"));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore variables for player id {}.", e, _objectId);
			return false;
		}
		finally
		{
			compareAndSetChanges(true, false);
		}
		return true;
	}
	
	@Override
	public boolean storeMe()
	{
		// No changes, nothing to store.
		if (!hasChanges())
			return false;
		
		try (Connection con = ConnectionPool.getConnection())
		{
			// Clear previous entries.
			try (PreparedStatement ps = con.prepareStatement(DELETE_QUERY))
			{
				ps.setInt(1, _objectId);
				ps.execute();
			}
			
			// Insert all variables.
			try (PreparedStatement ps = con.prepareStatement(INSERT_QUERY))
			{
				ps.setInt(1, _objectId);
				for (Entry<String, Object> entry : entrySet())
				{
					ps.setString(2, entry.getKey());
					ps.setString(3, String.valueOf(entry.getValue()));
					ps.addBatch();
				}
				ps.executeBatch();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't update variables for player id {}.", e, _objectId);
			return false;
		}
		finally
		{
			compareAndSetChanges(true, false);
		}
		return true;
	}
}