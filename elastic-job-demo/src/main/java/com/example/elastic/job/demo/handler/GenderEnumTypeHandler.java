package com.example.elastic.job.demo.handler;

import com.example.elastic.job.demo.enums.GenderEnum;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenderEnumTypeHandler implements TypeHandler<GenderEnum> {

    @Override
    public void setParameter(PreparedStatement ps, int i, GenderEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.value);
    }

    @Override
    public GenderEnum getResult(ResultSet rs, String columnName) throws SQLException {
        int gender = rs.getInt(columnName);
        return GenderEnum.valueOf(gender);
    }

    @Override
    public GenderEnum getResult(ResultSet rs, int columnIndex) throws SQLException {
        int gender = rs.getInt(columnIndex);
        return GenderEnum.valueOf(gender);
    }

    @Override
    public GenderEnum getResult(CallableStatement cs, int columnIndex) throws SQLException {
        int gender = cs.getInt(columnIndex);
        return GenderEnum.valueOf(gender);
    }
}
