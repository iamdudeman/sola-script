package technology.sola.script.parser;

import technology.sola.script.tokenizer.Token;

import java.util.List;

public class Parser {
  public final List<Token> tokens;
  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public ParserResult parse() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
