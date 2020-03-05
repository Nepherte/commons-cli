module nepherte.commons.cli {
  // require only java base.
  requires java.base;

  // export visible api only.
  exports com.nepherte.commons.cli;
  exports com.nepherte.commons.cli.parser;
  exports com.nepherte.commons.cli.exception;
}