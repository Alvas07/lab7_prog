package common.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public final class ObjectEncoder {
  public static ByteBuffer encodeObject(Object object) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(object);
    oos.flush();

    ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

    return buffer;
  }
}
