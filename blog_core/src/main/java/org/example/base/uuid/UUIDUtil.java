package org.example.base.uuid;

import java.util.UUID;

/*
 * @description:
 * @author: moki
 * @date: 2024/12/25 15:21
 * @param: 数据库UUID主键，在数据库中UUID类型为BINARY(16)，所以需要转换
 * @return:
 **/

public class UUIDUtil {
    public static byte[] uuidToBytes(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (15 - i));
        }
        return buffer;
    }
    public static byte[] uuidToBytes(String uid) {
        UUID uuid = UUID.fromString(uid);
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (15 - i));
        }
        return buffer;
    }

    public static UUID bytesToUUID(byte[] bytes) {
        long msb = 0;
        long lsb = 0;
        assert bytes.length == 16 : "数组长度必须为16字节";
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (bytes[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (bytes[i] & 0xff);
        }
        return new UUID(msb, lsb);
    }

    public static UUID generateUUID() {
        return UUID.randomUUID();
    }
}
