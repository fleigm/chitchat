package de.fleigm.chitchat;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.function.Supplier;

@ApplicationScoped
public class Transaction {

  @Transactional
  public void run(Runnable f) {
    f.run();
  }

  @Transactional
  public <T> T run(Supplier<T> f) {
    return f.get();
  }
}
