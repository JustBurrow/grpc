package kr.lul.grpc.util.common;

import com.google.protobuf.Message;

import java.util.Collection;

/**
 * 일반적인 Java 오브젝트의 Proto Buffers 메시지를 만든다.
 *
 * @author justburrow
 * @since 2019-05-25
 */
public interface MessageBuilder {
  /**
   * @return 메시지를 생성할 수 있는 자바 타입.
   */
  Collection<Class> supportsSourceTypes();

  /**
   * 자바 오브젝트에 해당하는 Proto Buffers 메시지를 생성한다.
   *
   * @param source 원본 자바 오브젝트.
   * @param <S>    원본 자바 오브젝트 타입.
   * @param <M>    Proto Buffers 메시지 타입.
   *
   * @return Proto Buffers 메시지.
   */
  <S, M extends Message> M build(S source);
}