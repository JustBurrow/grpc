package kr.lul.grpc.util.common;

import com.google.protobuf.Message;

import java.util.Collection;

/**
 * Proto Buffers 메시지에 해당하는 Java 오브젝트를 생성한다.
 *
 * @author justburrow
 * @since 2019-05-25
 */
public interface MessageParser {
  /**
   * @return 자바 오브젝트를 생성할 수 있는 메시지 타입.
   */
  Collection<Class<? extends Message>> supportsMessageTypes();

  /**
   * Proto Buffers 메시지에 해당하는 자바 오브젝트를 생성한다.
   *
   * @param message Proto Buffers 메시지.
   * @param <T>     생성할 자바 오브젝트의 타입.
   * @param <M>     원본 Proto Buffers 메시지 타입.
   *
   * @return 메시지에 해당하는 자바 오브젝트.
   */
  <T, M extends Message> T parse(M message);
}