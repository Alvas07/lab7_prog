package common.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

public class ObjectDecoder {
  public static Object decodeObject(ByteBuffer receiveBuffer)
      throws IOException, ClassNotFoundException {
    receiveBuffer.flip();
    ByteArrayInputStream bais =
        new ByteArrayInputStream(receiveBuffer.array(), 0, receiveBuffer.capacity());
    ObjectInputStream ois = new ObjectInputStream(bais);
    return ois.readObject();
  }
}
