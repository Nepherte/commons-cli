package greeting;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test that covers {@link Greeter}.
 */
public class GreeterTest {

  @Test
  public void sayHello() {
    Greeter greeter = new Greeter();
    String greeting = greeter.sayHello("Nepherte");
    assertThat(greeting, is("Hello, Nepherte"));
  }
}