package org.example.base.mybatisplus;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @description: UUID 类型转换器，用于在 MyBatis 中将 Java 的 UUID 字符串与数据库中的二进制数据进行相互转换。
 * @author: moki
 * @date: 2024/12/25 22:19
 */
public class UuidToBinaryTypeHandler extends BaseTypeHandler<String> {

    /**
     * 设置非空参数方法，将 UUID 字符串转换为字节数组并设置到 PreparedStatement 中。
     *
     * @param ps          PreparedStatement 对象，用于执行 SQL 语句。
     * @param i           参数索引位置。
     * @param parameter   要设置的 UUID 字符串。
     * @param jdbcType    JDBC 类型（通常为 null）。
     * @throws SQLException 如果设置参数时发生 SQL 异常。
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        if (parameter != null) {
            // 将 UUID 字符串转换为字节数组
            byte[] bytes = toBytes(UUID.fromString(parameter));
            // 将字节数组设置到 PreparedStatement 中指定的位置
            ps.setBytes(i, bytes);
        }
    }

    /**
     * 从 ResultSet 中根据列名获取结果，并将其转换为 UUID 字符串。
     *
     * @param rs          ResultSet 对象，包含查询结果。
     * @param columnName  列名。
     * @return            返回 UUID 字符串或 null（如果字节数组为空）。
     * @throws SQLException 如果获取结果时发生 SQL 异常。
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从 ResultSet 中获取字节数组
        byte[] bytes = rs.getBytes(columnName);
        // 检查字节数组是否为空，若不为空则转换为 UUID 字符串并返回
        return bytes == null ? null : fromBytes(bytes).toString();
    }

    /**
     * 从 ResultSet 中根据列索引获取结果，并将其转换为 UUID 字符串。
     *
     * @param rs             ResultSet 对象，包含查询结果。
     * @param columnIndex    列索引位置。
     * @return               返回 UUID 字符串或 null（如果字节数组为空）。
     * @throws SQLException  如果获取结果时发生 SQL 异常。
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 从 ResultSet 中获取字节数组
        byte[] bytes = rs.getBytes(columnIndex);
        // 检查字节数组是否为空，若不为空则转换为 UUID 字符串并返回
        return bytes == null ? null : fromBytes(bytes).toString();
    }

    /**
     * 从 CallableStatement 中根据列索引获取结果，并将其转换为 UUID 字符串。
     *
     * @param cs             CallableStatement 对象，用于执行存储过程。
     * @param columnIndex    列索引位置。
     * @return               返回 UUID 字符串或 null（如果字节数组为空）。
     * @throws SQLException  如果获取结果时发生 SQL 异常。
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 从 CallableStatement 中获取字节数组
        byte[] bytes = cs.getBytes(columnIndex);
        // 检查字节数组是否为空，若不为空则转换为 UUID 字符串并返回
        return bytes == null ? null : fromBytes(bytes).toString();
    }

    /**
     * 将 UUID 对象转换为 16 字节的字节数组。
     *
     * @param uuid   要转换的 UUID 对象。
     * @return       返回 16 字节的字节数组。
     */
    private byte[] toBytes(UUID uuid) {
        // 获取 UUID 的高位和低位
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        // 创建一个长度为 16 的字节数组
        byte[] buffer = new byte[16];
        // 使用位移操作将高位和低位分别存储到字节数组中
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
            buffer[i + 8] = (byte) (lsb >>> 8 * (7 - i));
        }
        return buffer;
    }

    /**
     * 将 16 字节的字节数组转换为 UUID 对象。
     *
     * @param bytes  要转换的字节数组。
     * @return       返回新的 UUID 对象。
     * @throws IllegalArgumentException 如果字节数组为空或长度不是 16。
     */
    private UUID fromBytes(byte[] bytes) {
        // 检查字节数组是否为空或长度是否为 16
        if (bytes == null || bytes.length != 16) {
            throw new IllegalArgumentException("Invalid byte array length for UUID");
        }
        // 初始化两个长整型变量用于存储高位和低位
        long msb = 0;
        long lsb = 0;
        // 使用位移操作将字节数组中的值还原为高位和低位
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (bytes[i] & 0xFF);
            lsb = (lsb << 8) | (bytes[i + 8] & 0xFF);
        }
        // 返回新的 UUID 对象
        return new UUID(msb, lsb);
    }
}
