package minhtbth08199.dao;

import fourSome.model.UserRequirements;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.*;

/** DAO cho wizard nhu cầu. Tên cột cập nhật luôn bị giới hạn bởi whitelist. */
public class UserRequirementsDAO {
    private static final Set<String> FIELDS = new HashSet<>(Arrays.asList(
        "target_resolution", "target_fps", "graphics_preset", "budget", "cpu_priority",
        "gpu_priority", "desired_ram_gb", "desired_storage_gb", "design_style", "wants_rgb", "optimize_cost", "analysis_note", "status"));
    public UserRequirements findById(int id) {
        try (Connection c=DBConnect.getConnection(); PreparedStatement p=c.prepareStatement("SELECT * FROM user_requirements WHERE requirement_id=?")) {
            p.setInt(1,id); try(ResultSet r=p.executeQuery()){ return r.next()?map(r):null; }
        } catch(SQLException e){ throw new IllegalStateException("Không đọc được nhu cầu",e); }
    }
    public int create(Integer userId) {
        String sql="INSERT INTO user_requirements (user_id,status) VALUES (?, 'DRAFT')";
        try(Connection c=DBConnect.getConnection(); PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            if(userId==null)p.setNull(1,Types.INTEGER);else p.setInt(1,userId); p.executeUpdate();
            try(ResultSet k=p.getGeneratedKeys()){if(k.next())return k.getInt(1);}
        }catch(SQLException e){throw new IllegalStateException("Không tạo được nhu cầu",e);} throw new IllegalStateException("Không có requirement_id");
    }
    public boolean updateField(int id,String field,Object value){
        if(!FIELDS.contains(field)) throw new IllegalArgumentException("Trường cập nhật không hợp lệ");
        try(Connection c=DBConnect.getConnection(); PreparedStatement p=c.prepareStatement("UPDATE user_requirements SET "+field+"=? WHERE requirement_id=?")){
            p.setObject(1,value);p.setInt(2,id);return p.executeUpdate()==1;
        }catch(SQLException e){throw new IllegalStateException("Không lưu được nhu cầu",e);}
    }
    public boolean complete(int id,String note){ updateField(id,"analysis_note",note);return updateField(id,"status","COMPLETED"); }
    private UserRequirements map(ResultSet r)throws SQLException{UserRequirements x=new UserRequirements();x.setRequirementId(r.getInt("requirement_id"));x.setUserId(r.getInt("user_id"));x.setTargetResolution(r.getString("target_resolution"));x.setTargetFps(r.getString("target_fps"));x.setGraphicsPreset(r.getString("graphics_preset"));x.setBudget(r.getDouble("budget"));x.setCpuPriority(r.getInt("cpu_priority"));x.setGpuPriority(r.getInt("gpu_priority"));x.setDesiredRamGb(r.getInt("desired_ram_gb"));x.setDesiredStorageGb(r.getInt("desired_storage_gb"));x.setDesignStyle(r.getString("design_style"));x.setWantsRgb(r.getBoolean("wants_rgb"));x.setOptimizeCost(r.getBoolean("optimize_cost"));x.setAnalysisNote(r.getString("analysis_note"));x.setStatus(r.getString("status"));return x;}
}
