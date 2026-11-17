package minhtbth08199.dao;
import fourSome.url.DBConnect;
import java.sql.*; import java.util.*;
public class UserRequirementGamesDAO {
 public List<Integer> findGameIds(int requirementId){List<Integer>x=new ArrayList<>();try(Connection c=DBConnect.getConnection();PreparedStatement p=c.prepareStatement("SELECT game_id FROM user_requirement_games WHERE requirement_id=? AND status='ACTIVE'")){p.setInt(1,requirementId);try(ResultSet r=p.executeQuery()){while(r.next())x.add(r.getInt(1));}}catch(SQLException e){throw new IllegalStateException("Không đọc được game",e);}return x;}
 public void replaceGames(int requirementId,List<Integer> ids){try(Connection c=DBConnect.getConnection()){c.setAutoCommit(false);try(PreparedStatement d=c.prepareStatement("DELETE FROM user_requirement_games WHERE requirement_id=?")){d.setInt(1,requirementId);d.executeUpdate();}try(PreparedStatement i=c.prepareStatement("INSERT INTO user_requirement_games(requirement_id,game_id,status) VALUES (?,?,'ACTIVE')")){for(Integer id:ids){i.setInt(1,requirementId);i.setInt(2,id);i.addBatch();}i.executeBatch();}c.commit();}catch(SQLException e){throw new IllegalStateException("Không lưu được game",e);}}
}
